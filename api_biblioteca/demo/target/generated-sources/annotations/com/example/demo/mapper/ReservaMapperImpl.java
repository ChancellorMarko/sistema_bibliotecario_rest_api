package com.example.demo.mapper;

import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-31T16:29:30-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ReservaMapperImpl implements ReservaMapper {

    @Override
    public ReservaDTO toDTO(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }

        ReservaDTO reservaDTO = new ReservaDTO();

        reservaDTO.setClienteId( reserva.getClienteId() );
        reservaDTO.setDataReserva( reserva.getDataReserva() );
        reservaDTO.setId( reserva.getId() );
        reservaDTO.setLivroId( reserva.getLivroId() );
        reservaDTO.setStatus( reserva.getStatus() );

        return reservaDTO;
    }

    @Override
    public Reserva toEntity(ReservaDTO reservaDTO) {
        if ( reservaDTO == null ) {
            return null;
        }

        Reserva reserva = new Reserva();

        reserva.setClienteId( reservaDTO.getClienteId() );
        reserva.setDataReserva( reservaDTO.getDataReserva() );
        reserva.setId( reservaDTO.getId() );
        reserva.setLivroId( reservaDTO.getLivroId() );
        reserva.setStatus( reservaDTO.getStatus() );

        return reserva;
    }

    @Override
    public List<ReservaDTO> toDTOList(List<Reserva> reservas) {
        if ( reservas == null ) {
            return null;
        }

        List<ReservaDTO> list = new ArrayList<ReservaDTO>( reservas.size() );
        for ( Reserva reserva : reservas ) {
            list.add( toDTO( reserva ) );
        }

        return list;
    }
}
