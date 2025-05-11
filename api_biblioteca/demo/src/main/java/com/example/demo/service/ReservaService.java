package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Reserva;
import com.example.demo.repository.IReservaRepository;

@Service
public class ReservaService {
    
    @Autowired
    private IReservaRepository reservaRepository;

    public Reserva salvar(Reserva reserva)
    {
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> buscarPorId(Long id)
    {
        return reservaRepository.findById(id);
    }

    public Reserva atualizar(Long id, Reserva atualizarReserva)
    {
        Reserva reservaExistente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva não encontrada!"));

        reservaExistente.setClienteId(atualizarReserva.getClienteId());
        reservaExistente.setLivroId(atualizarReserva.getLivroId());
        reservaExistente.setDataReserva(atualizarReserva.getDataReserva());
        reservaExistente.setStatus(atualizarReserva.getStatus());

        return reservaRepository.save(reservaExistente);
    }

    public void deletar(Long id)
    {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> listarTodos()
    {
        return reservaRepository.findAll();
    }
}
