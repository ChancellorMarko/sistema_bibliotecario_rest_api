package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Emprestimo;

@Repository
public interface IEmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByClienteId(Long clienteId);
    List<Emprestimo> findByStatus(String status);

    // Realizar a busca dos empréstimos atrasados por meio de uma Query no banco de dados
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucao < CURRENT_TIMESTAMP AND e.status <> 'CONCLUIDO'")
    List<Emprestimo> findAtrasados();

    @Query("SELECT e FROM Emprestimo e WHERE e.livro.id = :livroId AND e.status = 'EM_ANDAMENTO'")
    List<Emprestimo> findEmprestimosAtivosPorLivro(Long livroId);

    boolean existsByLivroIdAndStatus(Long livroId, String status);
}