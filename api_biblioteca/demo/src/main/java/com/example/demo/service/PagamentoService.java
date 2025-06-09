package com.example.demo.service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Pagamento;
import com.example.demo.dto.PagamentoDTO;
import com.example.demo.mapper.PagamentoMapper;
import com.example.demo.repository.IClienteRepository;
import com.example.demo.repository.IPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private IPagamentoRepository pagamentoRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    // Listar todos os pagamentos
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    // Buscar por ID
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    // Salvar entidade Pagamento diretamente
    public Pagamento salvar(Pagamento pagamento) {
        Cliente cliente = clienteRepository.findById(pagamento.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        pagamento.setCliente(cliente);
        return pagamentoRepository.save(pagamento);
    }

    // Salvar com DTO: mapeia, valida, seta data e status padrão
    public PagamentoDTO salvar(PagamentoDTO pagamentoDTO) {
        Cliente cliente = clienteRepository.findById(pagamentoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pagamento pagamento = pagamentoMapper.toEntity(pagamentoDTO);
        pagamento.setCliente(cliente);

        // Se não tem data, usar a atual
        if (pagamento.getDataPagamento() == null) {
            pagamento.setDataPagamento(LocalDateTime.now());
        }

        // Se não tem status ou é inválido, usar PENDENTE
        String status = pagamento.getStatus();
        if (status == null || status.isBlank() || status.equalsIgnoreCase("string")) {
            pagamento.setStatus("PENDENTE");
        }

        Pagamento pagamentoSalvo = salvar(pagamento);
        return pagamentoMapper.toDTO(pagamentoSalvo);
    }

    // Atualizar pagamento
    public PagamentoDTO atualizar(PagamentoDTO pagamentoDTO) {
        Pagamento pagamentoExistente = buscarPorId(pagamentoDTO.getId());
        
        Cliente cliente = clienteRepository.findById(pagamentoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Atualizar campos
        pagamentoExistente.setValor(pagamentoDTO.getValor());
        pagamentoExistente.setFormaPagamento(pagamentoDTO.getFormaPagamento());
        pagamentoExistente.setStatus(pagamentoDTO.getStatus());
        pagamentoExistente.setDataPagamento(pagamentoDTO.getDataPagamento());
        pagamentoExistente.setCliente(cliente);

        Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamentoExistente);
        return pagamentoMapper.toDTO(pagamentoAtualizado);
    }

    // Deletar pagamento
    public void deletar(Long id) {
        Pagamento pagamento = buscarPorId(id);
        pagamentoRepository.delete(pagamento);
    }

    // Listar por status
    public List<Pagamento> listarPorStatus(String status) {
        return pagamentoRepository.findByStatus(status);
    }

    public List<Pagamento> listarPorCliente(Long clienteId) {
        return pagamentoRepository.findByClienteId(clienteId);
    }

    public Pagamento atualizarStatus(Long id, String status) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        pagamento.setStatus(status);
        return pagamentoRepository.save(pagamento);
    }
}
