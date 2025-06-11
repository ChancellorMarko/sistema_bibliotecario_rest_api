package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Emprestimo;
import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;

@Component
public class MultaMapper {
    
    private static final Logger logger = LoggerFactory.getLogger(MultaMapper.class);

    public MultaDTO toDTO(Multa multa) {
        if (multa == null) {
            logger.warn("Tentativa de converter multa nula para DTO");
            return null;
        }

        try {
            MultaDTO dto = new MultaDTO();
            dto.setId(multa.getId());
            
            if (multa.getCliente() != null) {
                dto.setClienteId(multa.getCliente().getId());
            } else {
                logger.warn("Cliente nulo na multa ID: {}", multa.getId());
            }
            
            if (multa.getEmprestimo() != null) {
                dto.setEmprestimoId(multa.getEmprestimo().getId());
            } else {
                logger.warn("Empréstimo nulo na multa ID: {}", multa.getId());
            }
            
            dto.setValor(multa.getValor());
            dto.setDataMulta(multa.getDataMulta());
            dto.setDataPagamento(multa.getDataPagamento());
            
            logger.debug("Multa convertida para DTO com sucesso: {}", dto);
            return dto;
        } catch (Exception e) {
            logger.error("Erro ao converter multa para DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter multa para DTO: " + e.getMessage(), e);
        }
    }

    public Multa toEntity(MultaDTO dto) {
        if (dto == null) {
            logger.warn("Tentativa de converter DTO nulo para multa");
            return null;
        }

        try {
            Multa multa = new Multa();
            multa.setId(dto.getId());
            multa.setValor(dto.getValor());
            multa.setDataMulta(dto.getDataMulta());
            multa.setDataPagamento(dto.getDataPagamento());

            // Cliente e Empréstimo serão definidos no Service
            if (dto.getClienteId() != null) {
                Cliente cliente = new Cliente();
                cliente.setId(dto.getClienteId());
                multa.setCliente(cliente);
            }

            if (dto.getEmprestimoId() != null) {
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(dto.getEmprestimoId());
                multa.setEmprestimo(emprestimo);
            }
            
            logger.debug("DTO convertido para multa com sucesso: {}", multa);
            return multa;
        } catch (Exception e) {
            logger.error("Erro ao converter DTO para multa: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter DTO para multa: " + e.getMessage(), e);
        }
    }

    public List<MultaDTO> toDTOList(List<Multa> multas) {
        if (multas == null) {
            logger.warn("Tentativa de converter lista nula de multas para DTOs");
            return null;
        }

        try {
            List<MultaDTO> dtos = multas.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            
            logger.debug("Lista de multas convertida para DTOs com sucesso. Tamanho: {}", dtos.size());
            return dtos;
        } catch (Exception e) {
            logger.error("Erro ao converter lista de multas para DTOs: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter lista de multas para DTOs: " + e.getMessage(), e);
        }
    }
}
