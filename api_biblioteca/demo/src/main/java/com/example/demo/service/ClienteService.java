package com.example.demo.service;

import com.example.demo.Entities.Cliente;
import com.example.demo.repository.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private IClienteRepository clienteRepository;

    public Cliente salvar(Cliente cliente) {
        if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new RuntimeException("Documento já cadastrado");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    // Mudança principal: trocar Optional por tratamento de exceção
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id); // Reaproveita a lógica de busca

        if (!clienteExistente.getDocumento().equals(clienteAtualizado.getDocumento())) {
            if (clienteRepository.existsByDocumento(clienteAtualizado.getDocumento())) {
                throw new RuntimeException("Este documento já está cadastrado para outro cliente");
            }
            clienteExistente.setDocumento(clienteAtualizado.getDocumento());
        }

        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
            if (clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
                throw new RuntimeException("Email já cadastrado para outro cliente");
            }
            clienteExistente.setEmail(clienteAtualizado.getEmail());
        }

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(clienteExistente);
    }

    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
}