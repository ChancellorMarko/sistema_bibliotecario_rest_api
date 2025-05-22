package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LivroDTO {
    private Long livro_id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    private String autor;

    @NotBlank(message = "O ISBN é obrigatório")
    @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
    private String isbn;

    @NotNull(message = "A quantidade é obrigatória")
    private Integer quantidade;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    private String categoria;
}
