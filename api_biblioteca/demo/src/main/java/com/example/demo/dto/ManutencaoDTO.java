package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManutencaoDTO {
    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "O status da manutenção é obrigatório")
    private String status;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @NotNull(message = "O ID do livro é obrigatório")
    private Long livroId;
}