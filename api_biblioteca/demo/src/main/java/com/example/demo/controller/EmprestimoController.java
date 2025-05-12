package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.service.EmprestimoService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "Empréstimos", description = "Endpoints para gerenciamento de empréstimos")
@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {
    @Autowired
    private EmprestimoService emprestimoService;

    @Operation(summary = "Lista todos os empréstimos", description = "Retorna uma lista com todos os empréstimos já cadastrados")
    @GetMapping
    public ResponseEntity<List<EmprestimoDTO>> listarEmprestimos()
    {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarTodos();
        return ResponseEntity.ok(emprestimos);
    }

    @Operation(summary = "Busca um empréstimo por id", description = "Retorna um empréstimo cadastrados")
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoDTO> buscarPorId(@PathVariable Long id)
    {
        Optional<EmprestimoDTO> emprestimoDTO = emprestimoService.buscarPorId(id);
        return emprestimoDTO.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cria um novo empréstimo", description = "Cadastra um novo empréstimo no banco de dados")
    @PostMapping
    public ResponseEntity<ApiResponse<EmprestimoDTO>> criarEmprestimo(@Valid @RequestBody EmprestimoDTO emprestimoDTO) {
        try
        {
            // Tentar salvar o empréstimo
            EmprestimoDTO savedEmprestimo = emprestimoService.salvar(emprestimoDTO);

            ApiResponse<EmprestimoDTO> response = new ApiResponse<>(savedEmprestimo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch(IllegalArgumentException e)
        {
            // Cria uma mensagem de erro
            ErrorResponse errorResponse = new ErrorResponse("Argumento inválido", e.getMessage());
            ApiResponse<EmprestimoDTO> response = new ApiResponse<>(errorResponse);
            return ResponseEntity.badRequest().body(response);
        }
        catch(Exception e)
        {
            // Cria erro genérico
            ErrorResponse errorResponse = new ErrorResponse("Erro interno", e.getMessage());
            ApiResponse<EmprestimoDTO> response = new ApiResponse<>(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Deleta um empréstimo por id", description = "Cadastra um novo empréstimo no banco de dados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmprestimo(@PathVariable Long id)
    {
        emprestimoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
