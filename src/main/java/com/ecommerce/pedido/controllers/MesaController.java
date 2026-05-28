package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.MesaRequestDTO;
import com.ecommerce.pedido.dtos.MesaResponseDTO;
import com.ecommerce.pedido.services.MesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @PostMapping
    public ResponseEntity<MesaResponseDTO> criarMesa(@Valid @RequestBody MesaRequestDTO requestDTO) {
        MesaResponseDTO response = mesaService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> listarMesas(@RequestParam Long restauranteId) {
        return ResponseEntity.ok(mesaService.listarPorRestaurante(restauranteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> buscarMesaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> atualizarMesa(
            @PathVariable Long id,
            @Valid @RequestBody MesaRequestDTO requestDTO) {
        return ResponseEntity.ok(mesaService.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMesa(@PathVariable Long id) {
        mesaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
