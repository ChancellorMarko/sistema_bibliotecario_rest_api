package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Multa;

@Repository
public interface IMultaRepository extends JpaRepository<Multa, Long> {
    // Buscar por cliente
    List<Multa> findByClienteId(Long clienteId);

    // Buscar por empréstimo
    List<Multa> findByEmprestimoId(Long emprestimoId);
    
    // Buscar multas pendentes (não pagas)
    List<Multa> findByDataPagamentoIsNull();
    
    // Buscar multas pagas
    List<Multa> findByDataPagamentoIsNotNull();
    
    // Buscar multas pendentes por cliente
    List<Multa> findByClienteIdAndDataPagamentoIsNull(Long clienteId);
    
    // Buscar multas pagas por cliente
    List<Multa> findByClienteIdAndDataPagamentoIsNotNull(Long clienteId);
    
    // Consulta personalizada para multas pendentes por cliente
    @Query("SELECT m FROM Multa m WHERE m.cliente.id = :clienteId AND m.dataPagamento IS NULL")
    List<Multa> findMultasPendentesByCliente(@Param("clienteId") Long clienteId);
    
    // Consulta personalizada para verificar se cliente tem multas pendentes
    @Query("SELECT COUNT(m) > 0 FROM Multa m WHERE m.cliente.id = :clienteId AND m.dataPagamento IS NULL")
    boolean clienteTemMultasPendentes(@Param("clienteId") Long clienteId);
}
