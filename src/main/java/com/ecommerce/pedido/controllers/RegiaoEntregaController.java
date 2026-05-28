package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.RegiaoEntregaRequestDTO;
import com.ecommerce.pedido.dtos.RegiaoEntregaResponseDTO;
import com.ecommerce.pedido.services.RegiaoEntregaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/restaurantes/{restauranteId}/regioes")
public class RegiaoEntregaController {

    private final RegiaoEntregaService regiaoEntregaService;

    public RegiaoEntregaController(RegiaoEntregaService regiaoEntregaService) {
        this.regiaoEntregaService = regiaoEntregaService;
    }

    @GetMapping
    public ResponseEntity<List<RegiaoEntregaResponseDTO>> listarRegioes(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(regiaoEntregaService.listar(restauranteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegiaoEntregaResponseDTO> buscarRegiaoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(regiaoEntregaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RegiaoEntregaResponseDTO> criarRegiao(
            @PathVariable Long restauranteId,
            @Valid @RequestBody RegiaoEntregaRequestDTO requestDTO) {
        RegiaoEntregaResponseDTO response = regiaoEntregaService.criar(restauranteId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegiaoEntregaResponseDTO> atualizarRegiao(
            @PathVariable Long id,
            @Valid @RequestBody RegiaoEntregaRequestDTO requestDTO) {
        return ResponseEntity.ok(regiaoEntregaService.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarRegiao(@PathVariable Long id) {
        regiaoEntregaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
