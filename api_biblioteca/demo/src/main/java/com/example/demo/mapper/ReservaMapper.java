package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;

@Mapper(componentModel = "spring")
public interface ReservaMapper {
    // Mapeando explicitamente o nomes das variáveis
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "livroId", source = "livro.id")
    ReservaDTO toDTO(Reserva reserva);

    // Ignorando o mapeamento para não gerar erros ao tentar converter IDs Long para Classe Livro ou cliente
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "livro", ignore = true)
    @Mapping(target = "dataReserva", ignore = true)
    Reserva toEntity(ReservaDTO reservaDTO);

    List<ReservaDTO> toDTOList(List<Reserva> reservas);
}
