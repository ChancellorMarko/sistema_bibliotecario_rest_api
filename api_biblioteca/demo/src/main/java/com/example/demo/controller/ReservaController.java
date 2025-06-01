package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.ReservaDTO;
import com.example.demo.service.ReservaService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Tag(name = "Reservas", description = "Endpoints para o gerenciamento das reservas.")
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    // Listar todas
    @Operation(summary = "Lista todos as reservas", description = "Retorna uma lista com todas as reservas já cadastrados")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservaDTO>>> listarReservas()
    {
        List<ReservaDTO> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(new ApiResponse<>(reservas));
    }

    // Buscar por id
    @Operation(summary = "Busca uma reserva por id", description = "Retorna uma reserva cadastrada")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaDTO>> buscarPorid(@PathVariable Long id)
    {
        ReservaDTO reservaDTO = reservaService.buscarPorId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada"));

        return ResponseEntity.ok(new ApiResponse<>(reservaDTO));
    }

    // Criar nova reserva
    @Operation(summary = "Cria uma nova reserva", description = "Cadastra uma nova reserva no banco de dados")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservaDTO>> criarReserva(@Valid @RequestBody ReservaDTO reservaDTO)
    {
        try
        {
            // Tentar salvar reserva
            ReservaDTO savedReserva = reservaService.salvar(reservaDTO);
            // Retorna sucesso
            ApiResponse<ReservaDTO> response = new ApiResponse<>(savedReserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch(IllegalArgumentException e)
        {
            // Cria erro com mensagem específica
            ErrorResponse errorResponse = new ErrorResponse("Erro interno", e.getMessage());
            ApiResponse<ReservaDTO> response = new ApiResponse<>(errorResponse);
            return ResponseEntity.badRequest().body(response);
        }
        catch(Exception e)
        {
            // Cria erro genérico
            ErrorResponse errorResponse = new ErrorResponse("Erro interno", e.getMessage());
            ApiResponse<ReservaDTO> response = new ApiResponse<>(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Deleta uma reserva por id", description = "Deleta uma reserva no banco de dados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id)
    {
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
