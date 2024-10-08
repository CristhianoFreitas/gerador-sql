package br.com.geradorsql.domain.port.in;

import br.com.geradorsql.domain.model.GerarSqlParametros;

public interface GerarSqlUseCase {

    void gerarSql(GerarSqlParametros entrada);

}
