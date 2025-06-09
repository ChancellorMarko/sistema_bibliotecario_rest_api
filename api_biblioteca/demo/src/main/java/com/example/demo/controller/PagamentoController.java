package com.example.demo.controller;

import com.example.demo.dto.PagamentoDTO;
import com.example.demo.mapper.PagamentoMapper;
import com.example.demo.service.PagamentoService;
import com.example.demo.service.Utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pagamento", description = "Endpoints para gerenciamento de pagamentos")
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    // Listar todos os pagamentos
    @GetMapping
    @Operation(summary = "Lista todos os pagamentos")
    public ResponseEntity<ApiResponse<List<PagamentoDTO>>> listarTodos() {
        var lista = pagamentoService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTOList(lista)));
    }

    // Buscar pagamento por ID
    @GetMapping("/{id}")
    @Operation(summary = "Busca um pagamento por ID")
    public ResponseEntity<ApiResponse<PagamentoDTO>> buscarPorId(@PathVariable Long id) {
        try {
            var pagamento = pagamentoService.buscarPorId(id);
            return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTO(pagamento)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // Criar novo pagamento
    @PostMapping
    @Operation(summary = "Registra um novo pagamento")
    public ResponseEntity<ApiResponse<PagamentoDTO>> criar(@Valid @RequestBody PagamentoDTO dto) {
        try {
            var pagamentoSalvo = pagamentoService.salvar(dto);
            return ResponseEntity.status(201).body(ApiResponse.success(pagamentoSalvo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("BadRequest", e.getMessage()));
        }
    }

    // Atualizar pagamento
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pagamento")
    public ResponseEntity<ApiResponse<PagamentoDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody PagamentoDTO dto) {
        try {
            dto.setId(id);
            var pagamentoAtualizado = pagamentoService.atualizar(dto);
            return ResponseEntity.ok(ApiResponse.success(pagamentoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // Deletar pagamento
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um pagamento")
    public ResponseEntity<ApiResponse<String>> deletar(@PathVariable Long id) {
        try {
            pagamentoService.deletar(id);
            return ResponseEntity.ok(ApiResponse.success("Pagamento removido com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    // Listar por status
    @GetMapping("/status/{status}")
    @Operation(summary = "Lista pagamentos por status")
    public ResponseEntity<ApiResponse<List<PagamentoDTO>>> listarPorStatus(@PathVariable String status) {
        var lista = pagamentoService.listarPorStatus(status);
        return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTOList(lista)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Lista todos os pagamentos de um cliente")
    public ResponseEntity<ApiResponse<List<PagamentoDTO>>> listarPorCliente(@PathVariable Long clienteId) {
        var lista = pagamentoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTOList(lista)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de um pagamento")
    public ResponseEntity<ApiResponse<PagamentoDTO>> atualizarStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            var atualizado = pagamentoService.atualizarStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTO(atualizado)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }
}
