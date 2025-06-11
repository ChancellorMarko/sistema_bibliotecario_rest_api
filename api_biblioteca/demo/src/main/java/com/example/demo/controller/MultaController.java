package com.example.demo.controller;

import com.example.demo.dto.MultaDTO;
import com.example.demo.service.MultaService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Multas", description = "Endpoints para gerenciamento de multas")
@RestController
@RequestMapping("/api/multas")
public class MultaController {

    private static final Logger logger = LoggerFactory.getLogger(MultaController.class);

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
            logger.error("Erro ao criar multa: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao criar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(error));
        }
    }

    // 2. Listar todas as multas
    @Operation(summary = "Lista todas as multas")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultas() {
        try {
            List<MultaDTO> multas = multaService.listarTodos();
            logger.info("Listando todas as multas. Total encontrado: {}", multas.size());
            return ResponseEntity.ok(new ApiResponse<>(multas));
        } catch (Exception e) {
            logger.error("Erro ao listar multas: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao listar multas", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 3. Buscar por ID
    @Operation(summary = "Busca uma multa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MultaDTO>> buscarMultaPorId(@PathVariable Long id) {
        try {
            logger.info("Buscando multa com ID: {}", id);
            
            Optional<MultaDTO> multaOpt = multaService.buscarPorId(id);
            
            if (multaOpt.isPresent()) {
                MultaDTO multa = multaOpt.get();
                logger.info("Multa encontrada: {}", multa);
                return ResponseEntity.ok(new ApiResponse<>(multa));
            } else {
                logger.warn("Multa não encontrada com ID: {}", id);
                ErrorResponse error = new ErrorResponse("Multa não encontrada", "Multa não encontrada com ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar multa com ID {}: {}", id, e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao buscar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 4. Atualizar multa
    @Operation(summary = "Atualiza os dados de uma multa")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MultaDTO>> atualizarMulta(@PathVariable Long id,
            @Valid @RequestBody MultaDTO multaDTO) {
        try {
            MultaDTO multaAtualizada = multaService.atualizar(id, multaDTO);
            return ResponseEntity.ok(new ApiResponse<>(multaAtualizada));
        } catch (RuntimeException e) {
            logger.error("Erro ao atualizar multa: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Multa não encontrada", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));
        } catch (Exception e) {
            logger.error("Erro ao atualizar multa: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao atualizar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 5. Deletar multa
    @Operation(summary = "Deleta uma multa por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletarMulta(@PathVariable Long id) {
        try {
            multaService.deletar(id);
            return ResponseEntity.ok(new ApiResponse<>("Multa deletada com sucesso"));
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar multa: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Multa não encontrada", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));
        } catch (Exception e) {
            logger.error("Erro ao deletar multa: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao deletar multa", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 6. Listar por cliente
    @Operation(summary = "Lista todas as multas de um cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultasPorCliente(@PathVariable Long clienteId) {
        try {
            List<MultaDTO> multasDoCliente = multaService.listarPorCliente(clienteId);
            return ResponseEntity.ok(new ApiResponse<>(multasDoCliente));
        } catch (RuntimeException e) {
            logger.error("Erro ao listar multas por cliente: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Cliente não encontrado", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));
        } catch (Exception e) {
            logger.error("Erro ao listar multas por cliente: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao listar multas por cliente", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 7. Listar multas pendentes
    @Operation(summary = "Lista multas pendentes (não pagas)")
    @GetMapping("/pendentes")
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultasPendentes() {
        try {
            List<MultaDTO> multasPendentes = multaService.listarMultasPendentes();
            return ResponseEntity.ok(new ApiResponse<>(multasPendentes));
        } catch (Exception e) {
            logger.error("Erro ao listar multas pendentes: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao listar multas pendentes", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 8. Listar multas pagas
    @Operation(summary = "Lista multas pagas")
    @GetMapping("/pagas")
    public ResponseEntity<ApiResponse<List<MultaDTO>>> listarMultasPagas() {
        try {
            List<MultaDTO> multasPagas = multaService.listarMultasPagas();
            return ResponseEntity.ok(new ApiResponse<>(multasPagas));
        } catch (Exception e) {
            logger.error("Erro ao listar multas pagas: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao listar multas pagas", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 9. Marcar multa como paga
    @Operation(summary = "Marca uma multa como paga")
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<ApiResponse<MultaDTO>> marcarComoPaga(@PathVariable Long id) {
        try {
            MultaDTO multaPaga = multaService.marcarComoPaga(id);
            return ResponseEntity.ok(new ApiResponse<>(multaPaga));
        } catch (RuntimeException e) {
            logger.error("Erro ao marcar multa como paga: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Multa não encontrada", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));
        } catch (Exception e) {
            logger.error("Erro ao marcar multa como paga: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao marcar multa como paga", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }

    // 10. Criar multa por atraso
    @Operation(summary = "Cria multa automaticamente por atraso de empréstimo")
    @PostMapping("/atraso/{emprestimoId}")
    public ResponseEntity<ApiResponse<MultaDTO>> criarMultaPorAtraso(@PathVariable Long emprestimoId) {
        try {
            MultaDTO multaCriada = multaService.criarMultaPorAtraso(emprestimoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(multaCriada));
        } catch (RuntimeException e) {
            logger.error("Erro ao criar multa por atraso: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao criar multa por atraso", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(error));
        } catch (Exception e) {
            logger.error("Erro ao criar multa por atraso: {}", e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Erro ao criar multa por atraso", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }
}
