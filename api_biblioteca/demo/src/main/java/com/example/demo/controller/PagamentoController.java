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

    @PostMapping
    @Operation(summary = "Registra um novo pagamento")
    public ResponseEntity<ApiResponse<String>> criar(@Valid @RequestBody PagamentoDTO dto) {
        var pagamento = pagamentoMapper.toEntity(dto);
        pagamentoService.salvar(pagamento);
        return ResponseEntity.status(201).body(ApiResponse.success("Pagamento registrado com sucesso!"));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Lista todos os pagamentos de um cliente")
    public ResponseEntity<ApiResponse<List<PagamentoDTO>>> listarPorCliente(@PathVariable Long clienteId) {
        var lista = pagamentoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTOList(lista)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de um pagamento")
    public ResponseEntity<ApiResponse<?>> atualizarStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            var atualizado = pagamentoService.atualizarStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(pagamentoMapper.toDTO(atualizado)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }
}