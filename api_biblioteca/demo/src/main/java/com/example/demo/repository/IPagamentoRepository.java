package com.example.demo.repository;

import com.example.demo.Entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByClienteId(Long clienteId);
    List<Pagamento> findByStatus(String status);
}
