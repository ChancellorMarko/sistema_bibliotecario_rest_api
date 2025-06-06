package com.example.demo.service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Pagamento;
import com.example.demo.repository.IPagamentoRepository;
import com.example.demo.repository.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private IPagamentoRepository pagamentoRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    public Pagamento salvar(Pagamento pagamento) {
        Cliente cliente = clienteRepository.findById(pagamento.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        pagamento.setCliente(cliente);
        return pagamentoRepository.save(pagamento);
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