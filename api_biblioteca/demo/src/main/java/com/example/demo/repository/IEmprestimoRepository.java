package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.Emprestimo;
import java.util.List;
import java.time.LocalDateTime;



public interface IEmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    Optional<Emprestimo> findByClienteId(Long clienteId); // Esperando implementação
    Optional<Emprestimo> findByLivroId(Long livroId); // Esperando implementação
    Optional<Emprestimo> findByDataEmprestimo(LocalDateTime dataEmprestimo); // Esperando implementação
    Optional<Emprestimo> findByStatus(String status); // Esperando implementação
}
