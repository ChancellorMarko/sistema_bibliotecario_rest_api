package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagamentoDTO {
    private Long id;

    @NotNull(message = "O valor do pagamento é obrigatório")
    private Double valor;

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @NotBlank(message = "O status do pagamento é obrigatório")
    private String status;

    @NotNull(message = "A data do pagamento é obrigatória")
    private LocalDateTime dataPagamento;

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;
}