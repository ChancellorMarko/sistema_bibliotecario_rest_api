package com.example.demo.mapper;

import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-16T19:24:33-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class MultaMapperImpl implements MultaMapper {

    @Override
    public MultaDTO toDTO(Multa multa) {
        if ( multa == null ) {
            return null;
        }

        MultaDTO multaDTO = new MultaDTO();

        multaDTO.setClienteId( multa.getClienteId() );
        multaDTO.setDataMulta( multa.getDataMulta() );
        multaDTO.setId( multa.getId() );
        multaDTO.setLivroId( multa.getLivroId() );
        multaDTO.setValorMulta( multa.getValorMulta() );

        return multaDTO;
    }

    @Override
    public Multa toEntity(MultaDTO multaDTO) {
        if ( multaDTO == null ) {
            return null;
        }

        Multa multa = new Multa();

        multa.setClienteId( multaDTO.getClienteId() );
        multa.setDataMulta( multaDTO.getDataMulta() );
        multa.setId( multaDTO.getId() );
        multa.setLivroId( multaDTO.getLivroId() );
        multa.setValorMulta( multaDTO.getValorMulta() );

        return multa;
    }

    @Override
    public List<MultaDTO> toDTOList(List<Multa> multas) {
        if ( multas == null ) {
            return null;
        }

        List<MultaDTO> list = new ArrayList<MultaDTO>( multas.size() );
        for ( Multa multa : multas ) {
            list.add( toDTO( multa ) );
        }

        return list;
    }
}
