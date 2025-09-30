package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.RestauranteRequestDTO;
import com.ecommerce.pedido.dtos.RestauranteResponseDTO;
import com.ecommerce.pedido.services.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    /**
     * Endpoint para criar um novo restaurante.
     * URL: POST /restaurantes
     */
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> criarRestaurante(@Valid @RequestBody RestauranteRequestDTO requestDTO) {
        RestauranteResponseDTO response = restauranteService.criar(requestDTO);
        //System.out.println("Recebido DTO para criar restaurante: " + requestDTO.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para buscar todos os restaurantes.
     * URL: GET /restaurantes
     */
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodosRestaurantes() {
        List<RestauranteResponseDTO> response = restauranteService.listarTodos();
        //System.out.println("Listando todos os restaurantes.");
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para buscar um restaurante pelo seu ID.
     * URL: GET /restaurantes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarRestaurantePorId(@PathVariable Long id) {
        RestauranteResponseDTO response = restauranteService.buscarPorId(id);
        //System.out.println("Buscando restaurante com ID: " + id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para atualizar um restaurante existente.
     * URL: PUT /restaurantes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizarRestaurante(
            @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO requestDTO) {
        RestauranteResponseDTO response = restauranteService.atualizar(id, requestDTO);
        //System.out.println("Atualizando restaurante com ID: " + id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para deletar um restaurante.
     * URL: DELETE /restaurantes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarRestaurante(@PathVariable Long id) {
        restauranteService.deletar(id);
        //System.out.println("Deletando restaurante com ID: " + id);
        return ResponseEntity.noContent().build();
    }
}