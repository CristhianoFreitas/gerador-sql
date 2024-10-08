package br.com.geradorsql.adapter.in.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.geradorsql.adapter.in.rest.dto.GerarSqlParametrosDto;
import br.com.geradorsql.domain.model.GerarSqlParametros;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GerarSqlParametrosMapper {

    private final ModelMapper modelMapper;

    public GerarSqlParametros mapearParaGerarSqlParametros(GerarSqlParametrosDto dto) {

        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, GerarSqlParametros.class);
    }
}
