package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;

@Mapper(componentModel = "spring")
public interface ReservaMapper {
    ReservaDTO toDTO(Reserva reserva);
    Reserva toEntity(ReservaDTO reservaDTO);
    List<ReservaDTO> toDTOList(List<Reserva> reservas);
}
