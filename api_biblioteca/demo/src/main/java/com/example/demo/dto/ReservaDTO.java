package com.example.demo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservaDTO {
    private Long id;

    @NotNull(message = "O id do cliente é obrigatório!")
    private Long clienteId;

    @NotNull(message = "O id do livro é obrigatório!")
    private Long livroId;

    @NotNull(message = "A data de empréstimo é obrigatória!")
    private LocalDateTime dataReserva;

    @NotBlank(message = "O status da reserva é obrigatório!")
    @Size(max = 20, message = "A descrição de status deve ter no máximo 20 caracteres")
    @Pattern(regexp = "ATIVA|CANCELADA|EXPIRADA", message = "Status deve ser ATIVA, CANCELADA ou EXPIRADA")
    private String status;
}
