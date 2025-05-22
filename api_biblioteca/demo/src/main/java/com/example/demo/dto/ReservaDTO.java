package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Livro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservaDTO {
    private Long id;

    @NotNull(message = "O id do cliente é obrigatório!")
    private Cliente clienteId;

    @NotNull(message = "O id do livro é obrigatório!")
    private Livro livroId;

    @NotNull(message = "A data de empréstimo é obrigatória!")
    private LocalDateTime dataReserva;

    @NotBlank(message = "O status da reserva é obrigatório!")
    @Size(max = 50, message = "A descrição de status deve ter no máximo 50 caracteres")
    private String status;
}
