package br.com.geradorsql.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class GerarSqlParametros {

    private String chavesTxt;
    private String dadosCsv;
    private String arquivoNaoEncontrados;
    private String chaveCsv;
    private Integer logIncremento;
    private List<Configuracao> configuracoes;
}
