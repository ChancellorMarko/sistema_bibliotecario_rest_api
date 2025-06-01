package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Reserva;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Long>{
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByStatus(String status);
    List<Reserva> findByLivroId(Long livroId);
}