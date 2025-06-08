package com.example.demo.controller;

import com.example.demo.dto.ManutencaoDTO;
import com.example.demo.mapper.ManutencaoMapper;
import com.example.demo.service.ManutencaoService;
import com.example.demo.service.Utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Manutenção", description = "Endpoints para gerenciamento de manutenções de livros")
@RestController
@RequestMapping("/api/manutencoes")
public class ManutencaoController {

    @Autowired
    private ManutencaoService manutencaoService;

    @Autowired
    private ManutencaoMapper manutencaoMapper;

    @PostMapping
    @Operation(summary = "Registrar uma nova manutenção")
    public ResponseEntity<ApiResponse<String>> criar(@Valid @RequestBody ManutencaoDTO dto) {
        var manutencao = manutencaoMapper.toEntity(dto);
        manutencaoService.salvar(manutencao);
        return ResponseEntity.status(201).body(ApiResponse.success("Manutenção registrada com sucesso!"));
    }

    @GetMapping("/andamento")
    @Operation(summary = "Listar manutenções em andamento")
    public ResponseEntity<ApiResponse<List<ManutencaoDTO>>> listarEmAndamento() {
        var lista = manutencaoService.listarEmAndamento();
        return ResponseEntity.ok(ApiResponse.success(manutencaoMapper.toDTOList(lista)));
    }

    @GetMapping("/livro/{livroId}")
    @Operation(summary = "Listar histórico de manutenções de um livro")
    public ResponseEntity<ApiResponse<List<ManutencaoDTO>>> listarPorLivro(@PathVariable Long livroId) {
        var lista = manutencaoService.listarPorLivro(livroId);
        return ResponseEntity.ok(ApiResponse.success(manutencaoMapper.toDTOList(lista)));
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Marcar manutenção como concluída")
    public ResponseEntity<ApiResponse<ManutencaoDTO>> concluir(@PathVariable Long id) {
        try {
            var concluida = manutencaoService.concluir(id);
            return ResponseEntity.ok(ApiResponse.success(manutencaoMapper.toDTO(concluida)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }
}
