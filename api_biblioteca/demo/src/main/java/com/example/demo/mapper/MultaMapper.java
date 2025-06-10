package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Livro;
import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;

@Component
public class MultaMapper {

    public MultaDTO toDTO(Multa multa) {
        if (multa == null) {
            return null;
        }

        MultaDTO dto = new MultaDTO();
        dto.setId(multa.getId());
        dto.setClienteId(multa.getCliente() != null ? multa.getCliente().getId() : null);
        dto.setLivroId(multa.getLivro() != null ? multa.getLivro().getId() : null);
        dto.setValorMulta(multa.getValor());
        dto.setDataMulta(multa.getDataMulta());
        dto.setStatus(multa.getStatus());
        dto.setDataPagamento(multa.getDataPagamento());

        return dto;
    }

    public Multa toEntity(MultaDTO dto) {
        if (dto == null) {
            return null;
        }

        Multa multa = new Multa();
        multa.setId(dto.getId());
        multa.setValor(dto.getValorMulta());
        multa.setDataMulta(dto.getDataMulta());
        multa.setStatus(dto.getStatus());
        multa.setDataPagamento(dto.getDataPagamento());

        // Cliente e Livro serão definidos no Service
        if (dto.getClienteId() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(dto.getClienteId());
            multa.setCliente(cliente);
        }

        if (dto.getLivroId() != null) {
            Livro livro = new Livro();
            livro.setId(dto.getLivroId());
            multa.setLivro(livro);
        }

        return multa;
    }

    public List<MultaDTO> toDTOList(List<Multa> multas) {
        if (multas == null) {
            return null;
        }

        return multas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
