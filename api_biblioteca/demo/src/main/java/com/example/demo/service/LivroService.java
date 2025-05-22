package com.example.demo.service;


import com.example.demo.Entities.Livro;
import com.example.demo.repository.ILivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private ILivroRepository livroRepository;

    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> buscarTodos() {
        return livroRepository.findAll();
    }

    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + id));
    }

    public Livro atualizar(Long id, Livro novoLivro) {
        Livro existente = buscarPorId(id);

        existente.setTitulo(novoLivro.getTitulo());
        existente.setAutor(novoLivro.getAutor());
        existente.setIsbn(novoLivro.getIsbn());
        existente.setQuantidade(novoLivro.getQuantidade());
        existente.setCategoria(novoLivro.getCategoria());

        return livroRepository.save(existente);
    }

    public void deletar(Long id) {
        Livro livro = buscarPorId(id);
        livroRepository.delete(livro);
    }
}