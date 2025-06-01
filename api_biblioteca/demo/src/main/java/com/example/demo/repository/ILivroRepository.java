package com.example.demo.repository;


import com.example.demo.Entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ILivroRepository extends JpaRepository<Livro, Long> {
 // Buscar livros com quantidade > 0
 List<Livro> findByQuantidadeGreaterThan(Integer quantidade);
}
