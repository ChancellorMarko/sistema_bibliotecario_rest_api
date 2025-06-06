package com.example.demo.controller;

import com.example.demo.Entities.Livro;
import com.example.demo.dto.LivroDTO;
import com.example.demo.mapper.LivroMapper;
import com.example.demo.service.LivroService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livro", description = "Endpoints para gerenciamento de livros")
@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @Autowired
    private LivroMapper livroMapper;

    @PostMapping
    @Operation(summary = "Adiciona um novo livro ao acervo")
    public ResponseEntity<ApiResponse<String>> criarLivro(@Valid @RequestBody LivroDTO livroDTO) {
        Livro livro = livroMapper.toEntity(livroDTO);
        livroService.salvar(livro);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Livro criado com sucesso!"));
    }

    @GetMapping
    @Operation(summary = "Lista todos os livros cadastrados")
    public ResponseEntity<ApiResponse<List<LivroDTO>>> listarLivros() {
        List<Livro> livros = livroService.buscarTodos();
        List<LivroDTO> dtos = livroMapper.toDTOList(livros);
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista livros com quantidade disponível > 0")
    public ResponseEntity<ApiResponse<List<LivroDTO>>> listarLivrosDisponiveis() {
        List<Livro> livros = livroService.buscarLivrosDisponiveis();
        List<LivroDTO> dtos = livroMapper.toDTOList(livros);
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{livro_id}")
    @Operation(summary = "Retorna os detalhes de um livro específico")
    public ResponseEntity<ApiResponse<?>> buscarPorId(@PathVariable Long livro_id) {
        try {
            Livro livro = livroService.buscarPorId(livro_id);
            LivroDTO dto = livroMapper.toDTO(livro);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    @PutMapping("/{livro_id}")
    @Operation(summary = "Atualiza as informações de um livro")
    public ResponseEntity<ApiResponse<?>> atualizar(@PathVariable Long livro_id, @Valid @RequestBody LivroDTO livroDTO) {
        try {
            Livro livro = livroMapper.toEntity(livroDTO);
            Livro atualizado = livroService.atualizar(livro_id, livro);
            LivroDTO dto = livroMapper.toDTO(atualizado);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }

    @DeleteMapping("/{livro_id}")
    @Operation(summary = "Remove um livro do acervo")
    public ResponseEntity<ApiResponse<String>> deletar(@PathVariable Long livro_id) {
        try {
            livroService.deletar(livro_id);
            return ResponseEntity.ok(ApiResponse.success("Livro excluído com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NotFoundException", e.getMessage()));
        }
    }
}