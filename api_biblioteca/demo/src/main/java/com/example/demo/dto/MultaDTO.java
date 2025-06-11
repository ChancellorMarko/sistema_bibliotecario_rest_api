package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MultaDTO {
    private Long id;

    @NotNull(message = "O id do cliente é obrigatório!")
    private Long clienteId;

    @NotNull(message = "O id do empréstimo é obrigatório!")
    private Long emprestimoId;

    @NotNull(message = "A data da multa é obrigatória!")
    private LocalDateTime dataMulta;

    @NotNull(message = "O valor da multa é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valor;
    
    private LocalDateTime dataPagamento;
    
    // Método helper para verificar se está paga (não serializado no JSON)
    @JsonIgnore
    public boolean isPaga() {
        return dataPagamento != null;
    }
    
    @Override
    public String toString() {
        return String.format("MultaDTO{id=%d, clienteId=%d, emprestimoId=%d, valor=%s, paga=%s}", 
            id, clienteId, emprestimoId, valor, isPaga());
    }
}
