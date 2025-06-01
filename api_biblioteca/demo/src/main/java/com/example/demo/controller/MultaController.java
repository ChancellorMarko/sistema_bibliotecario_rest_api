
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;
import com.example.demo.mapper.MultaMapper;
import com.example.demo.service.MultaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/multa")
public class MultaController {

    @Autowired
    private MultaService multaService;

    @Autowired
    private MultaMapper multaMapper;

    // 1.Criando a multa
    @Operation(summary = "Criando multa")
    @PostMapping
    public ResponseEntity<MultaDTO> criarMulta(@RequestBody MultaDTO MultaDTO) {
        var multa = multaMapper.toEntity(MultaDTO);
        var multaSalva = multaService.salvar(multa);
        return ResponseEntity.ok(multaMapper.toDTO(multaSalva));
    }

    // 2.Listar as multas
    @Operation(summary = "Listando todas as multas")
    @GetMapping
    public ResponseEntity<List<MultaDTO>> listarMultas() {
        var multas = multaService.buscarTodos();
        return ResponseEntity.ok(multaMapper.toDTOList(multas));
    }

    // 3.Buscar por ID
    @Operation(summary = "Buscando multa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MultaDTO> buscarPorId(@PathVariable Long id) {
        var multa = multaService.buscarPorId(id);
        return ResponseEntity.ok(multaMapper.toDTO(multa));
    }

    // 4.Atualizar multa
    @PutMapping("/{id}")
    @Operation(summary= "Atualiza as informações da multa")
    public ResponseEntity<MultaDTO> atualizar(@PathVariable Long id, @RequestBody MultaDTO multaDTO) {
        Multa multa = multaMapper.toEntity(multaDTO);
        Multa atualizado = multaService.atualizar(id, multa);
        return ResponseEntity.ok(multaMapper.toDTO(atualizado));
    }
 
    // 5.Deletar multa
    @Operation(summary = "Deletar multa por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        multaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

