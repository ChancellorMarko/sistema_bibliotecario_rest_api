package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "multa_id")
    private Long multaId;

    @Column(name = "valor_pago", nullable = false)
    private Double valorPago;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @Column(name = "forma_pagamento", nullable = false)
    private String metodoPagamento; // PIX, DINHEIRO, CARTAO_CREDITO, etc.

    @Column(nullable = false)
    private String status; // PENDENTE, PROCESSADO, CANCELADO

    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Para compatibilidade com o código existente
    public Double getValor() {
        return valorPago;
    }

    public void setValor(Double valor) {
        this.valorPago = valor;
    }

    public String getFormaPagamento() {
        return metodoPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.metodoPagamento = formaPagamento;
    }
}
