package com.example.demo.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

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


    @NotNull(message = "O id do livro é obrigatório!")
    private Long livroId;


    @NotNull(message = "A data da multa é obrigatória!")
    private LocalDateTime dataMulta;


    @NotNull(message = "O valor da multa é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valorMulta;


    private String status;
    
    private LocalDateTime dataPagamento;
}
