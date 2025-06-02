package com.example.demo.mapper;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Livro;
import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-02T19:10:43-0300",
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

        reservaDTO.setClienteId( reservaClienteId( reserva ) );
        reservaDTO.setLivroId( reservaLivroId( reserva ) );
        reservaDTO.setDataReserva( reserva.getDataReserva() );
        reservaDTO.setId( reserva.getId() );
        reservaDTO.setStatus( reserva.getStatus() );

        return reservaDTO;
    }

    @Override
    public Reserva toEntity(ReservaDTO reservaDTO) {
        if ( reservaDTO == null ) {
            return null;
        }

        Reserva reserva = new Reserva();

        reserva.setId( reservaDTO.getId() );
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

    private Long reservaClienteId(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }
        Cliente cliente = reserva.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long reservaLivroId(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }
        Livro livro = reserva.getLivro();
        if ( livro == null ) {
            return null;
        }
        Long id = livro.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
