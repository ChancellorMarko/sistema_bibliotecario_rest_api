package com.example.demo.mapper;

import com.example.demo.Entities.Manutencao;
import com.example.demo.dto.ManutencaoDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManutencaoMapper {

    @Mapping(source = "livro.id", target = "livroId")
    ManutencaoDTO toDTO(Manutencao manutencao);

    @Mapping(source = "livroId", target = "livro.id")
    Manutencao toEntity(ManutencaoDTO manutencaoDTO);

    List<ManutencaoDTO> toDTOList(List<Manutencao> lista);
}