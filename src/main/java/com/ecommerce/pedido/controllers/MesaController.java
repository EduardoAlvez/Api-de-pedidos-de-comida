package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.ItemCompartilhadoRequestDTO;
import com.ecommerce.pedido.dtos.ItemCompartilhadoResponseDTO;
import com.ecommerce.pedido.dtos.MesaRequestDTO;
import com.ecommerce.pedido.dtos.MesaResponseDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.services.ItemCompartilhadoService;
import com.ecommerce.pedido.services.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/mesas")
public class MesaController {

    private final MesaService mesaService;
    private final ItemCompartilhadoService itemCompartilhadoService;

    public MesaController(MesaService mesaService, ItemCompartilhadoService itemCompartilhadoService) {
        this.mesaService = mesaService;
        this.itemCompartilhadoService = itemCompartilhadoService;
    }

    @Operation(summary = "Criar mesa", description = "Cria uma nova mesa com um cliente responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mesa criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<MesaResponseDTO> criarMesa(@Valid @RequestBody MesaRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        MesaResponseDTO response = mesaService.criar(requestDTO, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar mesas", description = "Lista todas as mesas do restaurante do usuário logado")
    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> listarMesas() {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(mesaService.listarPorRestaurante(usuarioLogado));
    }

    @Operation(summary = "Buscar mesa por ID", description = "Retorna os detalhes de uma mesa específica")
    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> buscarMesaPorId(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(mesaService.buscarPorId(id, usuarioLogado));
    }

    @Operation(summary = "Atualizar mesa", description = "Atualiza os dados de uma mesa")
    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> atualizarMesa(
            @PathVariable Long id,
            @Valid @RequestBody MesaRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(mesaService.atualizar(id, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Deletar mesa", description = "Remove uma mesa")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMesa(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        mesaService.deletar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar item compartilhado", description = "Adiciona um item ao pool de itens compartilhados da mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item compartilhado criado"),
        @ApiResponse(responseCode = "404", description = "Mesa ou produto não encontrado")
    })
    @PostMapping("/{mesaId}/compartilhados")
    public ResponseEntity<ItemCompartilhadoResponseDTO> adicionarItemCompartilhado(
            @PathVariable Long mesaId,
            @Valid @RequestBody ItemCompartilhadoRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemCompartilhadoService.adicionar(mesaId, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Listar itens compartilhados", description = "Lista todos os itens compartilhados de uma mesa")
    @GetMapping("/{mesaId}/compartilhados")
    public ResponseEntity<List<ItemCompartilhadoResponseDTO>> listarItensCompartilhados(
            @PathVariable Long mesaId) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(itemCompartilhadoService.listar(mesaId, usuarioLogado));
    }

    @Operation(summary = "Atualizar item compartilhado", description = "Altera quantidade ou produto de um item compartilhado")
    @PutMapping("/{mesaId}/compartilhados/{itemId}")
    public ResponseEntity<ItemCompartilhadoResponseDTO> atualizarItemCompartilhado(
            @PathVariable Long mesaId,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemCompartilhadoRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(itemCompartilhadoService.atualizar(mesaId, itemId, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Remover item compartilhado", description = "Remove um item do pool de compartilhados da mesa")
    @DeleteMapping("/{mesaId}/compartilhados/{itemId}")
    public ResponseEntity<Void> removerItemCompartilhado(
            @PathVariable Long mesaId,
            @PathVariable Long itemId) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        itemCompartilhadoService.remover(mesaId, itemId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
