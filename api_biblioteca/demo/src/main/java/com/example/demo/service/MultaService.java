package com.example.demo.service;

import java.util.List;
import java.util.Optional;

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

@Service
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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + multaDTO.getClienteId()));

        // Buscar livro
        Livro livro = livroRepository.findById(multaDTO.getLivroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + multaDTO.getLivroId()));

        // Criar multa
        Multa multa = new Multa();
        multa.setCliente(cliente);
        multa.setLivro(livro);
        multa.setValor(multaDTO.getValorMulta());
        multa.setDataMulta(multaDTO.getDataMulta());
        multa.setStatus(multaDTO.getStatus() != null ? multaDTO.getStatus() : "PENDENTE");
        multa.setDataPagamento(multaDTO.getDataPagamento());

        // Salvar e retornar DTO
        return multaMapper.toDTO(multaRepository.save(multa));
    }

    // Atualizar multa
    public MultaDTO atualizar(Long id, MultaDTO multaDTO) {
        Multa multaExistente = multaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));

        multaExistente.setValor(multaDTO.getValorMulta());
        multaExistente.setDataMulta(multaDTO.getDataMulta());
        multaExistente.setStatus(multaDTO.getStatus());
        multaExistente.setDataPagamento(multaDTO.getDataPagamento());

        return multaMapper.toDTO(multaRepository.save(multaExistente));
    }

    // Deletar multa
    public void deletar(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));
        multaRepository.delete(multa);
    }

    // Listar todas as multas
    public List<MultaDTO> listarTodos() {
        return multaMapper.toDTOList(multaRepository.findAll());
    }

    // Listar multas por cliente
    public List<MultaDTO> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));

        return multaMapper.toDTOList(multaRepository.findByClienteId(clienteId));
    }

    // Listar multas por status
    public List<MultaDTO> listarPorStatus(String status) {
        return multaMapper.toDTOList(multaRepository.findByStatus(status));
    }
}
