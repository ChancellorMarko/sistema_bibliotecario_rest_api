package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.Emprestimo;

public interface IEmprestimoRepository extends JpaRepository<Emprestimo, Long> {
}
