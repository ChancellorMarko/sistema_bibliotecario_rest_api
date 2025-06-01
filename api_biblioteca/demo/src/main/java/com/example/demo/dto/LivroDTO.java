package com.example.demo.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LivroDTO {


    private Long id;


    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String titulo;


    @NotBlank(message = "O autor é obrigatório")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    private String autor;


    @NotBlank(message = "A editora é obrigatória") // NOVO CAMPO
    @Size(max = 100, message = "A editora deve ter no máximo 100 caracteres")
    private String editora;


    @NotNull(message = "O ano de publicação é obrigatório") // NOVO CAMPO
    @Min(value = 1000, message = "Ano de publicação inválido")
    @Max(value = 9999, message = "Ano de publicação inválido")
    private Integer anoPublicacao;


    @NotBlank(message = "O ISBN é obrigatório")
    @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
    private String isbn;


    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;


    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    private String categoria;
}
