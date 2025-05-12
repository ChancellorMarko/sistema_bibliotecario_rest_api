package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReservaDTO;
import com.example.demo.service.ReservaService;
import com.example.demo.service.Utils.ApiResponse;
import com.example.demo.service.Utils.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @Operation(summary = "Lista todos as reservas", description = "Retorna uma lista com todas as reservas já cadastrados")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas()
    {
        List<ReservaDTO> reservas = reservaService.listarTodos();
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Busca uma reserva por id", description = "Retorna uma reserva cadastrada")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarPorid(@PathVariable Long id)
    {
        Optional<ReservaDTO> reservaDTO  =reservaService.buscarPorId(id);
        return reservaDTO.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cria uam nova reserva", description = "Cadastra uma nova reserva no banco de dados")
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
