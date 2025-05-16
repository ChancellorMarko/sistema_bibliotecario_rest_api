package com.example.demo.controller;

import com.example.demo.Entities.Cliente;
import com.example.demo.dto.ClienteDTO;
import com.example.demo.mapper.ClienteMapper;
import com.example.demo.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Tag(name = "Cliente", description = "Endpoints para gerenciamento de clientes")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    // 1. Criar Cliente
    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        Cliente novoCliente = clienteService.salvar(cliente);
        ClienteDTO resposta = clienteMapper.toDTO(novoCliente);
        return ResponseEntity.status(201).body(resposta);
    }

    // 2. Buscar Cliente por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente por ID")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        return cliente
                .map(c -> ResponseEntity.ok(clienteMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Atualizar Cliente
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um cliente")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente clienteAtualizado = clienteMapper.toEntity(clienteDTO);
        Cliente cliente = clienteService.atualizar(id, clienteAtualizado);
        return ResponseEntity.ok(clienteMapper.toDTO(cliente));
    }

    // 4. Excluir Cliente
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // (opcional) Listar todos (deixe comentado se não for usar)
    /*
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clienteMapper.toDTOList(clientes));
    }
    */
}