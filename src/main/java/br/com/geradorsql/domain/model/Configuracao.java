/**
 *
 */
package br.com.geradorsql.domain.model;

import java.util.Map;

import lombok.Data;

@Data
public class Configuracao {

    private String operacao;
    private String arquivoSaida;
    private String tabela;
    private String chaveTabela;
    private Map<String, String> campos;
    private Map<String, String> camposFixos;
}
