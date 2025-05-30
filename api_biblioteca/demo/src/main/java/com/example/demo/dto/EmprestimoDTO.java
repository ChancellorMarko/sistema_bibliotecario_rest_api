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
public class EmprestimoDTO {
    private Long id;

    @NotBlank(message = "O id do cliente é obrigatório")
    private Cliente clienteId;

    @NotNull(message = "O id do livro é obrigatório!")
    private Livro livroId;

    @NotNull(message = "Data de empréstimo não deve estar vazia!")
    private LocalDateTime dataEmprestimo;

    @NotNull(message = "Data de devolução não deve estar vazia!")
    private LocalDateTime dataDevolucao;

    @NotBlank(message = "O status da reserva é obrigatório!")
    @Size(max = 50, message = "A descrição de status deve ter no máximo 50 caracteres")
    private String status;
}
