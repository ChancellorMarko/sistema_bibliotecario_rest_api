package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Emprestimo;
import com.example.demo.repository.IEmprestimoRepository;

@Service
public class EmprestimoService{

    @Autowired
    private IEmprestimoRepository emprestimoRepository;

    public Emprestimo salvar(Emprestimo emprestimo)
    {
        return emprestimoRepository.save(emprestimo);
    }

    public Optional<Emprestimo> buscarPorId(Long id)
    {
        return emprestimoRepository.findById(id);
    }

    public Emprestimo atualizar(Long id, Emprestimo atualizarEmprestimo)
    {
        Emprestimo emprestimoExistente = emprestimoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Emprestimo não encontrado!"));

        emprestimoExistente.setLivroId(atualizarEmprestimo.getLivroId());
        emprestimoExistente.setDataEmprestimo(atualizarEmprestimo.getDataEmprestimo());
        emprestimoExistente.setDataDevolucao(atualizarEmprestimo.getDataDevolucao());
        emprestimoExistente.setStatus(atualizarEmprestimo.getStatus());

        return emprestimoRepository.save(emprestimoExistente);
    }

    public void deletar(Long id)
    {
        emprestimoRepository.deleteById(id);
    }

    public List<Emprestimo> listarTodos()
    {
        return emprestimoRepository.findAll();
    }
}
