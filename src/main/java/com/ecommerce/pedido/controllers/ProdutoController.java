package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.ProdutoRequestDTO;
import com.ecommerce.pedido.dtos.ProdutoResponseDTO;
import com.ecommerce.pedido.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }
    /**
     * Endpoint para criar um novo produto para um restaurante.
     * O 'restauranteId' vem dentro do corpo da requisição (requestDTO).
     * URL: POST /produtos
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO requestDTO) {
        ProdutoResponseDTO response = produtoService.criar(requestDTO);
        //System.out.println("Criando produto '" + requestDTO.getNome() + "' para o restaurante ID " + requestDTO.getRestauranteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para buscar um produto específico pelo seu ID.
     * URL: GET /produtos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO response = produtoService.buscarPorId(id);
        //System.out.println("Buscando produto com ID: " + id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para atualizar um produto.
     * URL: PUT /produtos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO requestDTO) {
        ProdutoResponseDTO response = produtoService.atualizar(id, requestDTO);
        //System.out.println("Atualizando produto com ID: " + id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para deletar um produto.
     * URL: DELETE /produtos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        //System.out.println("Deletando produto com ID: " + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para listar todos os produtos DE UM RESTAURANTE ESPECÍFICO.
     * URL: GET /restaurantes/{restauranteId}/produtos
     */
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutosPorRestaurante(@PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> response = produtoService.listarPorRestaurante(restauranteId);
        //System.out.println("Listando produtos do restaurante ID: " + restauranteId);
        return ResponseEntity.ok().body(response);
    }
}