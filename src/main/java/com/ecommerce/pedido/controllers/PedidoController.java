package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.AtualizarStatusPedidoDTO;
import com.ecommerce.pedido.dtos.PedidoRequestDTO;
import com.ecommerce.pedido.dtos.PedidoResponseDTO;
import com.ecommerce.pedido.models.Usuario;
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

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO response = pedidoService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO response = pedidoService.buscarPorId(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPorUsuario(@PathVariable Long usuarioId) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        List<PedidoResponseDTO> response = pedidoService.listarPorUsuario(usuarioId, usuarioLogado);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoDTO statusDTO) {
        PedidoResponseDTO response = pedidoService.atualizarStatus(id, statusDTO.getNovoStatus());
        return ResponseEntity.ok().body(response);
    }
}