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
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "Empréstimos", description = "Endpoints para gerenciamento de empréstimos")
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {
    @Autowired
    private EmprestimoService emprestimoService;

    // Listar todos
    @Operation(summary = "Lista todos os empréstimos", description = "Retorna uma lista com todos os empréstimos já cadastrados")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmprestimoDTO>>> listarEmprestimos()
    {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarTodos();
        return ResponseEntity.ok(new ApiResponse<>(emprestimos));
    }

    // Buscar por id
    @Operation(summary = "Busca um empréstimo por id", description = "Retorna um empréstimo cadastrados")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmprestimoDTO>> buscarPorId(@PathVariable Long id)
    {
        EmprestimoDTO emprestimoDTO = emprestimoService.buscarPorId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));
        return ResponseEntity.ok(new ApiResponse<>(emprestimoDTO));
    }

    // Criar novo empréstimo
    @Operation(summary = "Cria um novo empréstimo", description = "Cadastra um novo empréstimo no banco de dados")
    @PostMapping
    public ResponseEntity<ApiResponse<EmprestimoDTO>> criarEmprestimo(@Valid @RequestBody EmprestimoDTO emprestimoDTO) {
        try
        {
            // Tentar salvar o empréstimo
            EmprestimoDTO savedEmprestimo = emprestimoService.salvar(emprestimoDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedEmprestimo));
        }
        catch(IllegalArgumentException e)
        {
            // Cria um erro com a mensagem específica
            ErrorResponse errorResponse = new ErrorResponse("Argumento inválido", e.getMessage());

            return ResponseEntity.badRequest().body(new ApiResponse<>(errorResponse));
        }
        catch(Exception e)
        {
            // Cria erro genérico
            ErrorResponse errorResponse = new ErrorResponse("Erro interno", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(errorResponse));
        }
    }

    // Atualiza o status de um empréstimo
    @Operation(summary = "Atualiza um empréstimo especifico", description = "Atualiza um empréstimo no banco de dados")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EmprestimoDTO>> atualizarStatusEmprestimo(@PathVariable Long id, @Valid @RequestBody EmprestimoDTO emprestimoDTO)
    {
        EmprestimoDTO emprestimoAtualizado = emprestimoService.atualizar(id, emprestimoDTO);
        return ResponseEntity.ok(ApiResponse.success(emprestimoAtualizado));
    }

    // Realiza a devolução de um livro
    @Operation(summary = "Registra uma devolução", description = "Registra uma devolução de um livro no banco de dados")
    @PatchMapping("/{id}/devolver")
    public ResponseEntity<ApiResponse<EmprestimoDTO>> fazerDevolucao(@PathVariable Long id)
    {
        EmprestimoDTO emprestimo = emprestimoService.registrarDevolucao(id);
        return ResponseEntity.ok(ApiResponse.success(emprestimo));
    }

    // Listar todos empréstimos por cliente
    @Operation(summary = "Lista todos empréstimos", description = "Lista todos os empréstimos feitos pelo cliente presentes no banco de dados")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<EmprestimoDTO>>> emprestimosPorCliente(@PathVariable long clienteId)
    {
        List<EmprestimoDTO> emprestimosDoCliente = emprestimoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success(emprestimosDoCliente));
    }

    // Listar empréstimos atrasados
    @Operation(summary = "Listar empréstimos atrasados", description = "Lista todos os empréstimos atrasados do banco de dados")
    @GetMapping("/atrasados")
    public ResponseEntity<ApiResponse<List<EmprestimoDTO>>> listarEmprestimosAtrasados()
    {
        List<EmprestimoDTO> emprestimosAtrasados = emprestimoService.listarTodosAtrasados();
        return ResponseEntity.ok(new ApiResponse<>(emprestimosAtrasados));
    }

    // Deleta um empréstimo
    @Operation(summary = "Deleta um empréstimo por id", description = "Deleta um empréstimo no banco de dados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmprestimo(@PathVariable Long id)
    {
        emprestimoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
