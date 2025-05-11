package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Emprestimo;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.mapper.EmprestimoMapper;
import com.example.demo.repository.IEmprestimoRepository;

@Service
public class EmprestimoService{

    @Autowired
    private IEmprestimoRepository emprestimoRepository;

    @Autowired
    private EmprestimoMapper emprestimoMapper;

    public Optional<EmprestimoDTO> buscarPorId(Long id)
    {
        return emprestimoRepository.findById(id).map(emprestimoMapper::toDTO);
    }

    public EmprestimoDTO salvar(EmprestimoDTO emprestimoDTO)
    {
        Emprestimo emprestimo = emprestimoMapper.toEntity(emprestimoDTO);
        return emprestimoMapper.toDTO(emprestimoRepository.save(emprestimo));
    }

    public void deletar(Long id)
    {
        emprestimoRepository.deleteById(id);
    }

    public List<EmprestimoDTO> listarTodos()
    {
        return emprestimoMapper.toDTOList(emprestimoRepository.findAll());
    }

    public EmprestimoDTO atualizar(Long id, EmprestimoDTO emprestimoDTO)
    {
        Emprestimo emprestimoExistente = emprestimoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));

        emprestimoExistente.setClienteId(emprestimoDTO.getClienteId());
        emprestimoExistente.setLivroId(emprestimoDTO.getLivroId());
        emprestimoExistente.setDataDevolucao(emprestimoDTO.getDataDevolucao());
        emprestimoExistente.setDataEmprestimo(emprestimoDTO.getDataEmprestimo());
        emprestimoExistente.setStatus(emprestimoDTO.getStatus());

        return emprestimoMapper.toDTO(emprestimoRepository.save(emprestimoExistente));
    }
}
