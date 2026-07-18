package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.RegiaoEntregaRequestDTO;
import com.ecommerce.pedido.dtos.RegiaoEntregaResponseDTO;
import com.ecommerce.pedido.models.Usuario;
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
    public ResponseEntity<List<RegiaoEntregaResponseDTO>> listarRegioes() {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(regiaoEntregaService.listar(usuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegiaoEntregaResponseDTO> buscarRegiaoPorId(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(regiaoEntregaService.buscarPorId(id, usuarioLogado));
    }

    @PostMapping
    public ResponseEntity<RegiaoEntregaResponseDTO> criarRegiao(
            @Valid @RequestBody RegiaoEntregaRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        RegiaoEntregaResponseDTO response = regiaoEntregaService.criar(requestDTO, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegiaoEntregaResponseDTO> atualizarRegiao(
            @PathVariable Long id,
            @Valid @RequestBody RegiaoEntregaRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(regiaoEntregaService.atualizar(id, requestDTO, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarRegiao(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        regiaoEntregaService.deletar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
