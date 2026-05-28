package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.ComandaRequestDTO;
import com.ecommerce.pedido.dtos.ComandaResponseDTO;
import com.ecommerce.pedido.dtos.RateioRequestDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.repositories.UsuarioRepository;
import com.ecommerce.pedido.services.ComandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1")
public class ComandaController {

    private final ComandaService comandaService;
    private final UsuarioRepository usuarioRepository;

    public ComandaController(ComandaService comandaService, UsuarioRepository usuarioRepository) {
        this.comandaService = comandaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/mesas/{mesaId}/comandas")
    public ResponseEntity<ComandaResponseDTO> criarComanda(
            @PathVariable Long mesaId,
            @Valid @RequestBody ComandaRequestDTO requestDTO,
            Authentication auth) {
        Usuario garcom = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        ComandaResponseDTO response = comandaService.criar(mesaId, garcom.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/comandas")
    public ResponseEntity<List<ComandaResponseDTO>> listarComandas(@RequestParam Long mesaId) {
        return ResponseEntity.ok(comandaService.listarPorMesa(mesaId));
    }

    @GetMapping("/comandas/{id}")
    public ResponseEntity<ComandaResponseDTO> buscarComanda(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.buscarPorId(id));
    }

    @PostMapping("/comandas/{id}/rateio")
    public ResponseEntity<ComandaResponseDTO> rateio(
            @PathVariable Long id,
            @Valid @RequestBody RateioRequestDTO requestDTO) {
        return ResponseEntity.ok(comandaService.rateio(id, requestDTO));
    }

    @PostMapping("/comandas/{id}/fechar")
    public ResponseEntity<ComandaResponseDTO> fecharComanda(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.fechar(id));
    }
}
