package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Multa;
import com.example.demo.repository.IMultaRepository;

@Service
public class MultaService {

    @Autowired
    private IMultaRepository multaRepository;

    public Multa salvar(Multa multa) {
        return multaRepository.save(multa);
    }

    public List<Multa> buscarTodos() {
        return multaRepository.findAll();
    }

    public Multa buscarPorId(Long id) {
        return multaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + id));
    }

    public Multa atualizar(Long id, Multa novaMulta) {
        Multa existente = buscarPorId(id);

        existente.setEmprestimoId(novaMulta.getEmprestimoId());
        existente.setValor(novaMulta.getValor());
        existente.setStatus(novaMulta.getStatus());
        existente.setDataPagamento(novaMulta.getDataPagamento());

        return multaRepository.save(existente);
    }

    public void deletar(Long id) {
        Multa multa = buscarPorId(id);
        multaRepository.delete(multa);
    }
} 
