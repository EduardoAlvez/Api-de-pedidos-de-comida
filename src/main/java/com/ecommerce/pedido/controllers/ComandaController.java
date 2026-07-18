package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.*;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.services.ComandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1")
public class ComandaController {

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @Operation(summary = "Criar comanda", description = "Cria uma comanda para um cliente na mesa (itens opcionais)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comanda criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/mesas/{mesaId}/comandas")
    public ResponseEntity<ComandaResponseDTO> criarComanda(
            @PathVariable Long mesaId,
            @Valid @RequestBody ComandaRequestDTO requestDTO) {
        Usuario garcom = SecurityUtils.getUsuarioLogado();
        ComandaResponseDTO response = comandaService.criar(mesaId, garcom.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar comandas", description = "Lista todas as comandas de uma mesa")
    @GetMapping("/comandas")
    public ResponseEntity<List<ComandaResponseDTO>> listarComandas(@RequestParam Long mesaId) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(comandaService.listarPorMesa(mesaId, usuarioLogado));
    }

    @Operation(summary = "Buscar comanda", description = "Retorna os detalhes de uma comanda")
    @GetMapping("/comandas/{id}")
    public ResponseEntity<ComandaResponseDTO> buscarComanda(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(comandaService.buscarPorId(id, usuarioLogado));
    }

    @Operation(summary = "Adicionar item na comanda", description = "Adiciona um item individual à comanda")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item adicionado"),
        @ApiResponse(responseCode = "404", description = "Comanda ou produto não encontrado")
    })
    @PostMapping("/comandas/{id}/itens")
    public ResponseEntity<ComandaItemResponseDTO> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ComandaItemRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comandaService.adicionarItem(id, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Atualizar item da comanda", description = "Altera a quantidade de um item na comanda")
    @PutMapping("/comandas/{comandaId}/itens/{itemId}")
    public ResponseEntity<ComandaItemResponseDTO> atualizarItem(
            @PathVariable Long comandaId,
            @PathVariable Long itemId,
            @Valid @RequestBody ComandaItemRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(comandaService.atualizarItem(comandaId, itemId, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Remover item da comanda", description = "Remove um item da comanda")
    @DeleteMapping("/comandas/{comandaId}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable Long comandaId,
            @PathVariable Long itemId) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        comandaService.removerItem(comandaId, itemId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rateio de item compartilhado", description = "Registra pagamento de parte ou total de um item compartilhado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rateio registrado"),
        @ApiResponse(responseCode = "400", description = "Valor excede saldo pendente")
    })
    @PostMapping("/comandas/{id}/rateio")
    public ResponseEntity<ComandaResponseDTO> rateio(
            @PathVariable Long id,
            @Valid @RequestBody RateioRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(comandaService.rateio(id, requestDTO, usuarioLogado));
    }

    @Operation(summary = "Fechar comanda", description = "Fecha uma comanda (marca como paga)")
    @PostMapping("/comandas/{id}/fechar")
    public ResponseEntity<ComandaResponseDTO> fecharComanda(
            @PathVariable Long id,
            @Valid @RequestBody FecharComandaRequestDTO requestDTO) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        return ResponseEntity.ok(comandaService.fechar(id, requestDTO.getFormaPagamento(), usuarioLogado));
    }
}
