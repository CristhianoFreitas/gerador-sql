package br.com.geradorsql.adapter.in.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class GerarSqlParametrosDto {

    private String chavesTxt;
    private String dadosCsv;
    private String arquivoNaoEncontrados;
    private String chaveCsv;
    private Integer logIncremento;
    private List<ConfiguracaoDto> configuracoes;
}
