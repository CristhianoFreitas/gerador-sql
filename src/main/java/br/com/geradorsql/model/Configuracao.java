/**
 *
 */
package br.com.geradorsql.model;

import java.util.Map;

public class Configuracao {

    private String operacao;

    private String arquivoSaida;

    private String tabela;

    private String chaveTabela;

    private Map<String, String> campos;

    private Map<String, String> camposFixos;

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getArquivoSaida() {
        return arquivoSaida;
    }

    public void setArquivoSaida(String arquivoSaida) {
        this.arquivoSaida = arquivoSaida;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getChaveTabela() {
        return chaveTabela;
    }

    public void setChaveTabela(String chaveTabela) {
        this.chaveTabela = chaveTabela;
    }

    public Map<String, String> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, String> campos) {
        this.campos = campos;
    }

    public Map<String, String> getCamposFixos() {
        return camposFixos;
    }

    public void setCamposFixos(Map<String, String> camposFixos) {
        this.camposFixos = camposFixos;
    }

    @Override
    public String toString() {
        return String.format("Configuracao [operacao=%s, arquivoSaida=%s, tabela=%s, chaveTabela=%s, campos=%s, camposFixos=%s]", operacao,
            arquivoSaida, tabela, chaveTabela, campos, camposFixos);
    }



}
