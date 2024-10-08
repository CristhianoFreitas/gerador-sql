package br.com.geradorsql.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.geradorsql.adapter.in.rest.dto.GerarSqlParametrosDto;
import br.com.geradorsql.adapter.in.rest.mapper.GerarSqlParametrosMapper;
import br.com.geradorsql.domain.port.in.GerarSqlUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/gerar-sql")
@RequiredArgsConstructor
public class GeradorSqlController {

    private final GerarSqlUseCase gerarSqlUseCase;
    private final GerarSqlParametrosMapper gerarSqlParametrosMapper;

    @PostMapping()
    public ResponseEntity<Void> gerarSql(@RequestBody GerarSqlParametrosDto entrada) {

        log.info("Iniciando controller com a entrada: {}.", entrada);
        gerarSqlUseCase.gerarSql(gerarSqlParametrosMapper.mapearParaGerarSqlParametros(entrada));
        return ResponseEntity.noContent().build();
    }

}
