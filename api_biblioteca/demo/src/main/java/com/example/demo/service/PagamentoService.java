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

        pagamento.setDataPagamento(LocalDateTime.now());

        String status = pagamento.getStatus();
        if (status == null || status.isBlank() || status.equalsIgnoreCase("string")) {
            pagamento.setStatus("PENDENTE");
        }

        Pagamento pagamentoSalvo = salvar(pagamento);
        return pagamentoMapper.toDTO(pagamentoSalvo);
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
