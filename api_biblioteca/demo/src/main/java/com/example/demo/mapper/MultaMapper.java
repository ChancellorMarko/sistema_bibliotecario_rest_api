package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;

@Mapper(componentModel = "spring")
public interface MultaMapper {

    MultaDTO toDTO(Multa multa);

    Multa toEntity(MultaDTO multaDTO);

    List<MultaDTO> toDTOList(List<Multa> multas);
}


