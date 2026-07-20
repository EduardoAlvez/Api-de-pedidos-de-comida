package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.configs.SecurityUtils;
import com.ecommerce.pedido.dtos.UsuarioRequestDTO;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/API/V1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para criar um novo usuário (cadastro público).
     * O papel (tipo) é forçado para CLIENTE — cadastro de DONO_RESTAURANTE
     * e GARCOM segue fluxos específicos.
     * URL: POST /usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para buscar um usuário pelo seu ID.
     * URL: GET /usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para atualizar um usuário existente.
     * O usuário só pode alterar sua própria conta.
     * O campo tipo não pode ser alterado por este endpoint.
     * URL: PUT /usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {

        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        UsuarioResponseDTO response = usuarioService.atualizar(id, requestDTO, usuarioLogado);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para deletar um usuário.
     * O usuário só pode deletar sua própria conta.
     * URL: DELETE /usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        Usuario usuarioLogado = SecurityUtils.getUsuarioLogado();
        usuarioService.deletar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
