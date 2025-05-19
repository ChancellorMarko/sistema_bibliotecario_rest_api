package com.example.demo.mapper;

import com.example.demo.Entities.Cliente;
import com.example.demo.dto.ClienteDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-19T20:17:41-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteDTO toDTO(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setEmail( cliente.getEmail() );
        clienteDTO.setEndereco( cliente.getEndereco() );
        clienteDTO.setId( cliente.getId() );
        clienteDTO.setNome( cliente.getNome() );
        clienteDTO.setTelefone( cliente.getTelefone() );

        return clienteDTO;
    }

    @Override
    public Cliente toEntity(ClienteDTO clienteDTO) {
        if ( clienteDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setEmail( clienteDTO.getEmail() );
        cliente.setEndereco( clienteDTO.getEndereco() );
        cliente.setId( clienteDTO.getId() );
        cliente.setNome( clienteDTO.getNome() );
        cliente.setTelefone( clienteDTO.getTelefone() );

        return cliente;
    }

    @Override
    public List<ClienteDTO> toDTOList(List<Cliente> clientes) {
        if ( clientes == null ) {
            return null;
        }

        List<ClienteDTO> list = new ArrayList<ClienteDTO>( clientes.size() );
        for ( Cliente cliente : clientes ) {
            list.add( toDTO( cliente ) );
        }

        return list;
    }
}
