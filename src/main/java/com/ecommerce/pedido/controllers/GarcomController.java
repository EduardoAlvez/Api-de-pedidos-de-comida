package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.services.VinculoGarcomService;
import com.ecommerce.pedido.services.exceptions.AcessoRestauranteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/V1/restaurantes/{restauranteId}/garcons")
public class GarcomController {

    private final VinculoGarcomService vinculoGarcomService;

    public GarcomController(VinculoGarcomService vinculoGarcomService) {
        this.vinculoGarcomService = vinculoGarcomService;
    }

    @PostMapping
    public ResponseEntity<Void> vincularGarcom(
            @PathVariable Long restauranteId,
            @RequestBody Map<String, Long> body) {
        Usuario donoLogado = SecurityUtils.getUsuarioLogado();
        if (donoLogado == null || donoLogado.getTipo() != Role.DONO_RESTAURANTE) {
            throw new AcessoRestauranteException("Apenas donos de restaurante podem vincular garcons.");
        }
        vinculoGarcomService.vincular(restauranteId, body.get("usuarioId"), donoLogado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> desvincularGarcom(
            @PathVariable Long restauranteId,
            @PathVariable Long usuarioId) {
        Usuario donoLogado = SecurityUtils.getUsuarioLogado();
        if (donoLogado == null || donoLogado.getTipo() != Role.DONO_RESTAURANTE) {
            throw new AcessoRestauranteException("Apenas donos de restaurante podem desvincular garcons.");
        }
        vinculoGarcomService.desvincular(restauranteId, usuarioId, donoLogado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarGarcons(@PathVariable Long restauranteId) {
        List<Usuario> garcons = vinculoGarcomService.listarGarcons(restauranteId);
        List<UsuarioResponseDTO> dtos = garcons.stream().map(g -> {
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setId(g.getId());
            dto.setNome(g.getNome());
            dto.setEmail(g.getEmail());
            dto.setTipo(g.getTipo());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
