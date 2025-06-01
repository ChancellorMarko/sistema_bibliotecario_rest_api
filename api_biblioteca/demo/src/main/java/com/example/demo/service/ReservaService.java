package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Livro;
import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReservaDTO;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.IClienteRepository;
import com.example.demo.repository.ILivroRepository;
import com.example.demo.repository.IReservaRepository;

@Service
public class ReservaService {
    // Repository
    @Autowired
    private IReservaRepository reservaRepository;
    @Autowired
    private IClienteRepository clienteRepository;
    @Autowired
    private ILivroRepository livroRepository;

    // Mapper
    @Autowired
    private ReservaMapper reservaMapper;

    // Método para buscar por id
    public Optional<ReservaDTO> buscarPorId(Long id)
    {
        return reservaRepository.findById(id).map(reservaMapper::toDTO);
    }

    // Método para atualizar
    public ReservaDTO atualizar(Long id, ReservaDTO reservaDTO)
    {
        Reserva reservaExistente = reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada!"));

        // Atualizar apenas os cmapos que fazem sentido
        reservaExistente.setDataReserva(reservaDTO.getDataReserva());
        reservaExistente.setStatus(reservaDTO.getStatus());

        return reservaMapper.toDTO(reservaRepository.save(reservaExistente));
    }

    // Método para salvar
    public ReservaDTO salvar(ReservaDTO reservaDTO)
    {
         // Buscando pelo cliente
        Cliente cliente = clienteRepository.findById(reservaDTO.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        //Buscando pelo livro
        Livro livro = livroRepository.findById(reservaDTO.getLivroId())
            .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));

        Reserva reserva = reservaMapper.toEntity(reservaDTO);
        reserva.setCliente(cliente);
        reserva.setLivro(livro);

        // Definir a data do empréstimo como a data/hora atual
        reserva.setDataReserva(LocalDateTime.now());

        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    // Método para deletar reserva
    public void deletar(Long id)
    {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada"));

        reservaRepository.deleteById(id);
    }

    // Método para listar todas as reservas
    public List<ReservaDTO> listarTodas()
    {
        return reservaMapper.toDTOList(reservaRepository.findAll());
    }
}
