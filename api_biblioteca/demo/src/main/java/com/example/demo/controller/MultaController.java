package com.example.demo.controller;

import com.example.demo.dto.MultaDTO;
import com.example.demo.service.MultaService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Multas", description = "Endpoints para gerenciamento de multas")
@RestController
@RequestMapping("/api/multas")
public class MultaController {

    @Autowired
    private MultaService multaService;

    // 1. Criar multa
    @Operation(summary = "Cria uma nova multa")
    @PostMapping
    public ResponseEntity<ApiResponse<MultaDTO>> criarMulta(@Valid @RequestBody MultaDTO multaDTO) {
        try {
            MultaDTO novaMulta = multaService.salvar(multaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(novaMulta));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Erro ao criar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(error));
        }
    }

    // 2. Listar todas as multas
    @Operation(summary = "Lista todas as multas")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultas() {
        List<MultaDTO> multas = multaService.listarTodos();
        return ResponseEntity.ok(new ApiResponse<>(multas));
    }

    // 3. Buscar por ID
    @Operation(summary = "Busca uma multa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MultaDTO>> buscarMultaPorId(@PathVariable Long id) {
        MultaDTO multa = multaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));
        return ResponseEntity.ok(new ApiResponse<>(multa));
    }

    // 4. Atualizar multa
    @Operation(summary = "Atualiza os dados de uma multa")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MultaDTO>> atualizarMulta(@PathVariable Long id,
            @Valid @RequestBody MultaDTO multaDTO) {
        try {
            MultaDTO multaAtualizada = multaService.atualizar(id, multaDTO);
            return ResponseEntity.ok(new ApiResponse<>(multaAtualizada));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Erro ao atualizar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(error));
        }
    }

    // 5. Deletar multa
    @Operation(summary = "Deleta uma multa por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMulta(@PathVariable Long id) {
        multaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Listar por cliente
    @Operation(summary = "Lista todas as multas de um cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultasPorCliente(@PathVariable Long clienteId) {
        List<MultaDTO> multasDoCliente = multaService.listarPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponse<>(multasDoCliente));
    }

    // 7. Listar por status
    @Operation(summary = "Lista multas por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultasPorStatus(@PathVariable String status) {
        List<MultaDTO> multasPorStatus = multaService.listarPorStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(multasPorStatus));
    }
}
