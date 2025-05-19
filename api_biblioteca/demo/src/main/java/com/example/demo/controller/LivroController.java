package com.example.demo.controller;

import com.example.demo.dto.LivroDTO;
import com.example.demo.service.LivroService;
import com.example.demo.mapper.LivroMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @Autowired
    private LivroMapper livroMapper;

     @PostMapping
    public ResponseEntity<LivroDTO> criarLivro(@RequestBody LivroDTO livroDTO) {
        var livro = livroMapper.toEntity(livroDTO);
        var livroSalvo = livroService.salvar(livro);
        return ResponseEntity.ok(livroMapper.toDTO(livroSalvo));
    }

    // Listar todos os livros
    @GetMapping
    public ResponseEntity<List<LivroDTO>> listarLivros() {
        var livros = livroService.buscarTodos();
        return ResponseEntity.ok(livroMapper.toDTOList(livros));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> buscarPorId(@PathVariable Long id) {
        var livro = livroService.buscarPorId(id);
        return ResponseEntity.ok(livroMapper.toDTO(livro));
    }

    // Atualizar livro
    @PutMapping("/{id}")
    public ResponseEntity<LivroDTO> atualizar(@PathVariable Long id, @RequestBody LivroDTO livroDTO) {
        var livro = livroMapper.toEntity(livroDTO);
        var atualizado = livroService.atualizar(id, livro);
        return ResponseEntity.ok(livroMapper.toDTO(atualizado));
    }

    // Deletar livro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

