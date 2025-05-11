package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Reserva;


@Repository
public interface IReservaRepository {
    Optional<Reserva> findByClienteId(Long clienteId); // Esperando implementação
    Optional<Reserva> findByLivroId(Long livroId); // Esperando implementação
    Optional<Reserva> findByDataReserva(LocalDateTime dataReserva); // Esperando implementação
    Optional<Reserva> findByStatus(String status); // Esperando implementação
}
