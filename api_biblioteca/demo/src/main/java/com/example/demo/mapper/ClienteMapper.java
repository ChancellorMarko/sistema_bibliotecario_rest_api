package com.example.demo.mapper;

import com.example.demo.Entities.Cliente;
import com.example.demo.dto.ClienteDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);
    Cliente toEntity(ClienteDTO clienteDTO);
    List<ClienteDTO> toDTOList(List<Cliente> clientes);
}
