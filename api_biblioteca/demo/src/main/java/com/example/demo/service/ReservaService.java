package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.IReservaRepository;

@Service
public class ReservaService {
    @Autowired
    private IReservaRepository reservaRepository;

    @Autowired
    private ReservaMapper reservaMapper;

    public Optional<ReservaDTO> buscarPorId(Long id)
    {
        return reservaRepository.findById(id).map(reservaMapper::toDTO);
    }

    public ReservaDTO atualizar(Long id, ReservaDTO reservaDTO)
    {
        Reserva reservaExistente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva não encontrada!"));

        reservaExistente.setClienteId(reservaDTO.getClienteId());
        reservaExistente.setLivroId(reservaDTO.getLivroId());
        reservaExistente.setDataReserva(reservaDTO.getDataReserva());
        reservaExistente.setStatus(reservaDTO.getStatus());

        return reservaMapper.toDTO(reservaRepository.save(reservaExistente));
    }

    public ReservaDTO salvar(ReservaDTO reservaDTO)
    {
        Reserva reserva = reservaMapper.toEntity(reservaDTO);
        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    public void deletar(Long id)
    {
        reservaRepository.deleteById(id);
    }

    public List<ReservaDTO> listarTodos()
    {
        return reservaMapper.toDTOList(reservaRepository.findAll());
    }
}
