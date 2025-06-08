package com.example.demo.controller;

import com.example.demo.Entities.Cliente;
import com.example.demo.dto.ClienteDTO;
import com.example.demo.mapper.ClienteMapper;
import com.example.demo.service.ClienteService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cliente", description = "Endpoints para gerenciamento de clientes")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    // Criar um novo cliente
    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<ApiResponse<String>> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        clienteService.salvar(cliente);
        return ResponseEntity.status(201).body(ApiResponse.success("Cliente criado com sucesso!"));
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente por ID")
    public ResponseEntity<ApiResponse<?>> buscarPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            ClienteDTO dto = clienteMapper.toDTO(cliente);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um cliente")
    public ResponseEntity<ApiResponse<?>> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            Cliente clienteAtualizado = clienteMapper.toEntity(clienteDTO);
            Cliente cliente = clienteService.atualizar(id, clienteAtualizado);
            ClienteDTO dto = clienteMapper.toDTO(cliente);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // Deletar cliente
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    public ResponseEntity<ApiResponse<String>> deletar(@PathVariable Long id) {
        try {
            clienteService.deletar(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente excluído com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // (Opcional) Listar todos os clientes
    
    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        List<ClienteDTO> dtos = clienteMapper.toDTOList(clientes);
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
    
}