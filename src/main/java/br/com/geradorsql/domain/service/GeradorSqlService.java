package br.com.geradorsql.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import br.com.geradorsql.domain.model.GerarSqlParametros;
import br.com.geradorsql.domain.port.in.GerarSqlUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeradorSqlService implements GerarSqlUseCase {

    private static final String ASPAS_INICIO_FIM_REGEX = "^\"|\"$";
    private static final String CSV_SEPARADOR = ",";
    private static final String SQL_SEPARADOR = ", ";
    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s = %s;";
    private static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String VALOR_SQL = "'%s'";
    private static final String CAMPO_VALOR_SQL = "%s = %s";

    public void gerarSql(GerarSqlParametros parametros) {
        StopWatch sw = new StopWatch();

        log.info("Iniciando execução.");

        var arquivoCsv = Paths.get(parametros.getDadosCsv());

        if (Files.notExists(arquivoCsv)) {
            throw new RuntimeException("O arquivo csv não foi encontrado.");
        }

        sw.start("lerArquivoCsv");
        var linhasStream = lerArquivoCsv(arquivoCsv);
        sw.stop();

        sw.start("processarConfiguracoes");
        var primeiraLinhaCsv = linhasStream.stream().findFirst().orElseThrow(() -> new RuntimeException("Arquivo .csv vazio."));
        var cabecalho =
            Stream.of(primeiraLinhaCsv.split(CSV_SEPARADOR)).map(valor -> valor.replaceAll(ASPAS_INICIO_FIM_REGEX, "")).toList();
        processarConfiguracoes(parametros, cabecalho);
        sw.stop();

        // mapa na qual a chave: nome do arquivo e o valor: lista de comandos sql
        Map<String, List<String>> conteudoSaida = new HashMap<>();

        var conteudoCsv = linhasStream.stream().skip(1);
        sw.start("processarConteudoCsv");
        processarConteudoCsv(parametros, cabecalho, conteudoSaida, conteudoCsv);
        sw.stop();

        sw.start("escreverArquivoSql");
        escreverArquivoSql(conteudoSaida);
        sw.stop();

        log.debug(sw.prettyPrint());
        log.info("Finalizando execução em {} segundos.", sw.getTotalTimeSeconds());
        conteudoSaida.forEach((chave, valor) -> log.info("Arquivo: {} com {} registros.", chave, valor.size()));
    }

    // Função para escrever blocos nos streams
    private void escreverArquivoSql(Map<String, List<String>> conteudoSaida) {
        conteudoSaida.forEach((arquivoSqlSaida, conteudoSqlSaida) -> {
            log.info("Iniciando a escrita no arquivo: {}.", arquivoSqlSaida);
            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(arquivoSqlSaida))) {
                conteudoSqlSaida.forEach(linhaSql -> {
                    try {
                        bw.write(linhaSql);
                        bw.newLine();
                    } catch (IOException e) {
                        log.error("Erro ao tentar escrever linha no arquivo!", e);
                    }
                });
            } catch (IOException e) {
                log.error("Erro ao tentar criar arquivo para escrita!", e);
            }
        });
        log.info("Finalizando a escrita nos arquivos .sql");
    }

    // Função principal para gerar os comandos SQL
    private void processarConteudoCsv(GerarSqlParametros parametros, List<String> cabecalho, Map<String, List<String>> conteudoSaida,
        Stream<String> conteudoCsv) {

        AtomicInteger linhaAtual = new AtomicInteger();
        conteudoCsv.forEach(linha -> {
            log.debug("Processando a linha: {}", linhaAtual.incrementAndGet());

            if (linha.isEmpty()) {
                log.info("Linha {} sem conteúdo, passando para a próxima.", linhaAtual);
                return;
            }

            var valores = Stream.of(linha.split(CSV_SEPARADOR)).map(this::prepararValores).toList();
            Map<String, String> valoresPorCampo =
                IntStream.range(0, cabecalho.size()).boxed().collect(Collectors.toMap(cabecalho::get, valores::get));

            parametros.getConfiguracoes().forEach(operacao -> {

                if (UPDATE.equals(operacao.getOperacao())) {

                    List<String> clausulas = new ArrayList<>();
                    operacao.getCampos()
                        .forEach((campoTabela, campoCsv) -> clausulas
                            .add(String.format(CAMPO_VALOR_SQL, campoTabela, valoresPorCampo.get(campoCsv))));
                    operacao.getCamposFixos()
                        .forEach((chave, valor) -> clausulas.add(String.format(CAMPO_VALOR_SQL, chave, String.format(VALOR_SQL, valor))));
                    String clausulasStr = String.join(SQL_SEPARADOR, clausulas);
                    String nomeDaChaveTabela = operacao.getChaveTabela();
                    String valorDaChaveTabela = valoresPorCampo.get(parametros.getChaveCsv());

                    String sql = String.format(UPDATE_SQL, operacao.getTabela(), clausulasStr, nomeDaChaveTabela, valorDaChaveTabela);
                    conteudoSaida.computeIfAbsent(operacao.getArquivoSaida(), v -> new ArrayList<>()).add(sql);
                    log.debug(sql);

                } else if (INSERT.equals(operacao.getOperacao())) {

                    List<String> colunasInsert = new ArrayList<>();
                    colunasInsert.add(operacao.getChaveTabela());
                    colunasInsert.addAll(operacao.getCampos().keySet());
                    colunasInsert.addAll(operacao.getCamposFixos().keySet());
                    var colunasStr = String.join(SQL_SEPARADOR, colunasInsert);

                    List<String> valoresInsert = new ArrayList<>();
                    valoresInsert.add(valoresPorCampo.get(parametros.getChaveCsv()));
                    operacao.getCampos().forEach((campoTabela, campoCsv) -> valoresInsert.add(valoresPorCampo.get(campoCsv)));
                    operacao.getCamposFixos().forEach((chave, valor) -> valoresInsert.add(String.format(VALOR_SQL, valor)));
                    var valoresStr = String.join(SQL_SEPARADOR, valoresInsert);

                    String sql = String.format(INSERT_SQL, operacao.getTabela(), colunasStr, valoresStr);
                    conteudoSaida.computeIfAbsent(operacao.getArquivoSaida(), v -> new ArrayList<>()).add(sql);
                    log.debug(sql);
                }
            });
        });
        log.info("Finalizado o processamento das linhas.");
    }

    // Função para pré-processar configurações
    private void processarConfiguracoes(GerarSqlParametros parametros, List<String> cabecalho) {
        if (parametros.getConfiguracoes() == null) {
            throw new RuntimeException("Não encontrada as 'configuracoes' na parametros.");
        }
        parametros.getConfiguracoes().forEach(configuracao -> {
            if (!INSERT.equals(configuracao.getOperacao()) && !UPDATE.equals(configuracao.getOperacao())) {
                throw new RuntimeException("Valor inválido para a variável 'operacao' na configuração. Use 'UPDATE' ou 'INSERT'.");
            }
            if (!configuracao.getCampos().values().stream().allMatch(cabecalho::contains)) {
                throw new RuntimeException("A configuração de parametros possui um campo que não foi encontrado no arquivo .csv.");
            }
            if (configuracao.getCamposFixos() == null) {
                configuracao.setCamposFixos(Collections.emptyMap());
            }
        });
    }

    // Função para ler o arquivo .csv
    private List<String> lerArquivoCsv(Path arquivoCsv) {
        log.info("Iniciando leitura do arquivo .csv");
        List<String> linhas = null;
        try (BufferedReader br = Files.newBufferedReader(arquivoCsv)) {
            linhas = br.lines().toList();
        } catch (IOException e) {
            log.error("Erro ao tentar ler arquivo!", e);
        }
        log.info("Finalizando leitura do arquivo .csv");
        return linhas;
    }

    // Função que trata os valores nulos e remove espaços em branco e aspas duplas
    private String prepararValores(String valor) {
        if (valor == null || "null".equals(valor)) {
            return "NULL";
        } else {
            return String.format(VALOR_SQL, valor.trim().replaceAll(ASPAS_INICIO_FIM_REGEX, ""));
        }
    }

}
