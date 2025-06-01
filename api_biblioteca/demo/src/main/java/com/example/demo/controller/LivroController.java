package com.example.demo.controller;


import com.example.demo.Entities.Livro;
import com.example.demo.dto.LivroDTO;
import com.example.demo.service.LivroService;
import com.example.demo.mapper.LivroMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livro", description = "Endpoints para gerenciamento de livros") 
public class LivroController {


    @Autowired
    private LivroService livroService;


    @Autowired
    private LivroMapper livroMapper;


    @PostMapping
    @Operation(summary = "Adiciona um novo livro ao acervo")
    public ResponseEntity<LivroDTO> criarLivro(@Valid @RequestBody LivroDTO livroDTO) {
        Livro livro = livroMapper.toEntity(livroDTO);
        Livro livroSalvo = livroService.salvar(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroMapper.toDTO(livroSalvo));
    }


    @GetMapping
    @Operation(summary = "Lista todos os livros cadastrados")
    public ResponseEntity<List<LivroDTO>> listarLivros() {
        List<Livro> livros = livroService.buscarTodos();
        return ResponseEntity.ok(livroMapper.toDTOList(livros));
    }


    @GetMapping("/disponiveis")
    @Operation(summary = "Lista livros com quantidade disponível > 0")
    public ResponseEntity<List<LivroDTO>> listarLivrosDisponiveis() {
        List<Livro> livros = livroService.buscarLivrosDisponiveis();
        return ResponseEntity.ok(livroMapper.toDTOList(livros));
    }


    @GetMapping("/{livro_id}")
    @Operation(summary = "Retorna os detalhes de um livro específico")
    public ResponseEntity<LivroDTO> buscarPorId(@PathVariable Long livro_id) {
        Livro livro = livroService.buscarPorId(livro_id);
        return ResponseEntity.ok(livroMapper.toDTO(livro));
    }


    @PutMapping("/{livro_id}")
    @Operation(summary = "Atualiza as informações de um livro")
    public ResponseEntity<LivroDTO> atualizar(
            @PathVariable Long livro_id, @Valid @RequestBody LivroDTO livroDTO) {
        Livro livro = livroMapper.toEntity(livroDTO);
        Livro atualizado = livroService.atualizar(livro_id, livro);
        return ResponseEntity.ok(livroMapper.toDTO(atualizado));
    }


    @DeleteMapping("/{livro_id}")
    @Operation(summary = "Remove um livro do acervo")
    public ResponseEntity<Void> deletar(@PathVariable Long livro_id) {
        livroService.deletar(livro_id);
        return ResponseEntity.noContent().build();
    }
}