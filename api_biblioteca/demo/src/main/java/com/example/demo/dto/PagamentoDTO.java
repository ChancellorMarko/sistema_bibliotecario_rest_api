package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagamentoDTO {
    private Long id;

    @NotNull(message = "O ID da multa é obrigatório")
    private Long multaId;

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "O valor do pagamento é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private Double valorPago;

    @NotNull(message = "A data do pagamento é obrigatória")
    private LocalDateTime dataPagamento;

    @NotBlank(message = "O método de pagamento é obrigatório")
    private String metodoPagamento; // PIX, DINHEIRO, CARTAO_CREDITO, etc.

    @NotBlank(message = "O status do pagamento é obrigatório")
    private String status; // PENDENTE, PROCESSADO, CANCELADO

    private String observacoes;

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
