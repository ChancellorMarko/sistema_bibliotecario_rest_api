package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Emprestimo;
import com.example.demo.Entities.Livro;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.mapper.EmprestimoMapper;
import com.example.demo.repository.IClienteRepository;
import com.example.demo.repository.IEmprestimoRepository;
import com.example.demo.repository.ILivroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmprestimoService{
    // Repository
    @Autowired
    private IEmprestimoRepository emprestimoRepository;
    @Autowired
    private IClienteRepository clienteRepository;
    @Autowired
    private ILivroRepository livroRepository;

    // Mapper
    @Autowired
    private EmprestimoMapper emprestimoMapper;

    // Método para buscar por ID
    public Optional<EmprestimoDTO> buscarPorId(Long id)
    {
        return emprestimoRepository.findById(id).map(emprestimoMapper::toDTO);
    }

    // Método para salvar
    public EmprestimoDTO salvar(EmprestimoDTO emprestimoDTO)
    {
        // Buscando pelo cliente
        Cliente cliente = clienteRepository.findById(emprestimoDTO.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        //Buscando pelo livro
        Livro livro = livroRepository.findById(emprestimoDTO.getLivroId())
            .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));

        Emprestimo emprestimo = emprestimoMapper.toEntity(emprestimoDTO);
        emprestimo.setCliente(cliente);
        emprestimo.setLivro(livro);

        // Diminui um a cada empréstimo feito por livro
        livro.setQuantidade(livro.getQuantidade() - 1);
        livroRepository.save(livro);

        return emprestimoMapper.toDTO(emprestimoRepository.save(emprestimo));
    }

    // Deleta um empréstimo
    public void deletar(Long id)
    {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emprestimo não encontrado"));

        // Liberar o livro que não foi devolvido
        if (!"CONCLUIDO".equals(emprestimo.getStatus())) {
            Livro livro = emprestimo.getLivro();
            livro.setQuantidade(livro.getQuantidade() + 1);
            livroRepository.save(livro);
        }

        emprestimoRepository.deleteById(id);
    }

    // Listar todos os empréstimos
    public List<EmprestimoDTO> listarTodos()
    {
        return emprestimoMapper.toDTOList(emprestimoRepository.findAll());
    }

    // Atualizar um empréstimo
    public EmprestimoDTO atualizar(Long id, EmprestimoDTO emprestimoDTO)
    {
        Emprestimo emprestimoExistente = emprestimoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));

        // Atualizar apenas os campos que fazem sentido
        emprestimoExistente.setDataDevolucao(emprestimoDTO.getDataDevolucao());
        emprestimoExistente.setStatus(emprestimoDTO.getStatus());

        return emprestimoMapper.toDTO(emprestimoRepository.save(emprestimoExistente));
    }

    // Listar todos empréstimos por cliente
    public List<EmprestimoDTO> listarPorCliente(Long clienteId)
    {
        return emprestimoMapper.toDTOList(emprestimoRepository.findByClienteId(clienteId));
    }

    // Listar todos empréstimos atrasados
    public List<EmprestimoDTO> listarTodosAtrasados()
    {
        // Utilizando localDate para retornar o dia atual
        return emprestimoMapper.toDTOList(emprestimoRepository.findAtrasados());
    }

    // Registrar a devolução de um livro
    public EmprestimoDTO registrarDevolucao(Long id)
    {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));

        // Faz a liberação do livro
        Livro livro = emprestimo.getLivro();
        livro.setQuantidade(livro.getQuantidade() + 1);
        livroRepository.save(livro);

        // Atualiza o status do livro para "livre"
        emprestimo.setStatus("CONCLUIDO");

        return emprestimoMapper.toDTO(emprestimoRepository.save(emprestimo));
    }
}
