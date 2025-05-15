package com.example.demo.controller;

import com.example.demo.Entities.Cliente;
import com.example.demo.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import java.util.List;
import java.util.Optional;

@Tag(name = "Cliente", description = "Endpoints para gerenciamento de clientes")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // 1. Criar Cliente
    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        Cliente novoCliente = clienteService.salvar(cliente);
        return ResponseEntity.status(201).body(novoCliente);
    }

    // 2. Buscar Cliente por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente por ID")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 3. Atualizar Cliente
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um cliente")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        Cliente cliente = clienteService.atualizar(id, clienteAtualizado);
        return ResponseEntity.ok(cliente);
    }

    // 4. Excluir Cliente
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // (opcional) Listar todos(Deixar comentado por emquanto)
    /*@GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }*/
}