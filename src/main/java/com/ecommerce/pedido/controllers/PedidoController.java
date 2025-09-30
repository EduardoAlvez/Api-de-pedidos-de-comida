package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.AtualizarStatusPedidoDTO;
import com.ecommerce.pedido.dtos.PedidoRequestDTO;
import com.ecommerce.pedido.dtos.PedidoResponseDTO;
import com.ecommerce.pedido.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController (PedidoService pedidoService){
        this.pedidoService = pedidoService;
    }
    /**
     * Endpoint principal para criar um novo pedido.
     * URL: POST /pedidos
     */
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO response = pedidoService.criar(requestDTO);
        //System.out.println("Recebida requisição para criar pedido no restaurante ID: " + requestDTO.getRestauranteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para buscar um pedido específico pelo seu ID.
     * URL: GET /pedidos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO response = pedidoService.buscarPorId(id);
        //System.out.println("Buscando pedido com ID: " + id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para listar os pedidos de um usuário específico.
     * URL: GET /usuarios/{usuarioId}/pedidos
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPorUsuario(@PathVariable Long usuarioId) {
        List<PedidoResponseDTO> response = pedidoService.listarPorUsuario(usuarioId);
        //System.out.println("Listando pedidos do usuário ID: " + usuarioId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para atualizar o STATUS de um pedido (ex: CONFIRMADO >> EM_PREPARACAO).
     * URL: PUT /pedidos/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoDTO statusDTO) {
        PedidoResponseDTO response = pedidoService.atualizarStatus(id, statusDTO.getNovoStatus());
        //System.out.println("Atualizando status do pedido ID " + id + " para " + statusDTO.getNovoStatus());
        return ResponseEntity.ok().body(response);
    }
}