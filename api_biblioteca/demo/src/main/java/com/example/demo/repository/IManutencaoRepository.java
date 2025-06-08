package com.example.demo.repository;


import com.example.demo.Entities.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface IManutencaoRepository extends JpaRepository<Manutencao, Long> {
List<Manutencao> findByStatus(String status);
List<Manutencao> findByLivroId(Long livroId);
}
