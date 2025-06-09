package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Livro;
import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;
import com.example.demo.mapper.MultaMapper;
import com.example.demo.repository.IClienteRepository;
import com.example.demo.repository.ILivroRepository;
import com.example.demo.repository.IMultaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MultaService {

    @Autowired
    private IMultaRepository multaRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private ILivroRepository livroRepository;

    @Autowired
    private MultaMapper multaMapper;

    // Buscar por ID
    public Optional<MultaDTO> buscarPorId(Long id) {
        return multaRepository.findById(id).map(multaMapper::toDTO);
    }

    // Salvar multa
    public MultaDTO salvar(MultaDTO multaDTO) {
        // Buscar cliente
        Cliente cliente = clienteRepository.findById(multaDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + multaDTO.getClienteId()));

        // Buscar livro
        Livro livro = livroRepository.findById(multaDTO.getLivroId())
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com ID: " + multaDTO.getLivroId()));

        // Mapear DTO para entidade
        Multa multa = multaMapper.toEntity(multaDTO);
        multa.setCliente(cliente);
        multa.setLivro(livro);
        multa.setStatus("PENDENTE");

        // Salvar e retornar DTO
        return multaMapper.toDTO(multaRepository.save(multa));
    }

    // Atualizar multa
    public MultaDTO atualizar(Long id, MultaDTO multaDTO) {
        Multa multaExistente = multaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multa não encontrada com ID: " + id));

        multaExistente.setValor(multaDTO.getValorMulta());
        multaExistente.setDataMulta(multaDTO.getDataMulta());
        multaExistente.setStatus(multaDTO.getStatus());

        return multaMapper.toDTO(multaRepository.save(multaExistente));
    }

    // Deletar multa
    public void deletar(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multa não encontrada com ID: " + id));
        multaRepository.delete(multa);
    }

    // Listar todas as multas
    public List<MultaDTO> listarTodos() {
        return multaMapper.toDTOList(multaRepository.findAll());
    }

    // Listar multas por cliente (caso deseje)
    public List<MultaDTO> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        return multaMapper.toDTOList(multaRepository.findByClienteId(clienteId));
    }
}
