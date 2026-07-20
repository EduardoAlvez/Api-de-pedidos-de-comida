package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.GarcomRequestDTO;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.services.VinculoGarcomService;
import com.ecommerce.pedido.services.exceptions.AcessoRestauranteException;
import jakarta.validation.Valid;
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

    /**
     * Criar um novo garçom e vinculá-lo ao restaurante em um único passo.
     * Body: { "nome": "...", "email": "...", "senha": "...", "telefone": "..." }
     * Retorna 201 Created com os dados do garçom criado (incluindo ID e tipo GARCOM).
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarGarcom(
            @PathVariable Long restauranteId,
            @Valid @RequestBody GarcomRequestDTO requestDTO) {
        Usuario donoLogado = SecurityUtils.getUsuarioLogado();
        if (donoLogado == null || donoLogado.getTipo() != Role.DONO_RESTAURANTE) {
            throw new AcessoRestauranteException("Apenas donos de restaurante podem criar garcons.");
        }
        UsuarioResponseDTO response = vinculoGarcomService.criarGarcom(requestDTO, restauranteId, donoLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Vincular um usuário GARCOM existente ao restaurante.
     * Body: { "usuarioId": 5 }
     */
    @PostMapping("/vincular")
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
