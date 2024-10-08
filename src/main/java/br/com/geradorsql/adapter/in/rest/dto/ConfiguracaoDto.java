/**
 *
 */
package br.com.geradorsql.adapter.in.rest.dto;

import java.util.Map;

import lombok.Data;

@Data
public class ConfiguracaoDto {

    private String operacao;
    private String arquivoSaida;
    private String tabela;
    private String chaveTabela;
    private Map<String, String> campos;
    private Map<String, String> camposFixos;
}
