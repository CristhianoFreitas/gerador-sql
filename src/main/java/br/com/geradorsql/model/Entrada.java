package br.com.geradorsql.model;

import java.util.List;

public class Entrada {

    private String chavesTxt;

    private String dadosCsv;

    private String arquivoNaoEncontrados;

    private String chaveCsv;

    private Integer logIncremento;

    private List<Configuracao> configuracoes;

    public String getChavesTxt() {
        return chavesTxt;
    }

    public void setChavesTxt(String chavesTxt) {
        this.chavesTxt = chavesTxt;
    }

    public String getDadosCsv() {
        return dadosCsv;
    }

    public void setDadosCsv(String dadosCsv) {
        this.dadosCsv = dadosCsv;
    }

    public String getArquivoNaoEncontrados() {
        return arquivoNaoEncontrados;
    }

    public void setArquivoNaoEncontrados(String arquivoNaoEncontrados) {
        this.arquivoNaoEncontrados = arquivoNaoEncontrados;
    }

    public String getChaveCsv() {
        return chaveCsv;
    }

    public void setChaveCsv(String chaveCsv) {
        this.chaveCsv = chaveCsv;
    }

    public Integer getLogIncremento() {
        return logIncremento;
    }

    public void setLogIncremento(Integer logIncremento) {
        this.logIncremento = logIncremento;
    }

    public List<Configuracao> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<Configuracao> configuracoes) {
        this.configuracoes = configuracoes;
    }

    @Override
    public String toString() {
        return String.format(
            "Entrada [chavesTxt=%s, dadosCsv=%s, arquivoNaoEncontrados=%s, chaveCsv=%s, logIncremento=%s, configuracoes=%s]", chavesTxt,
            dadosCsv, arquivoNaoEncontrados, chaveCsv, logIncremento, configuracoes);
    }

}
