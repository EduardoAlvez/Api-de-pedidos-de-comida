package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.UsuarioRequestDTO;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/API/V1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para criar um novo usuário.
     * URL: POST /usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        // Se a requisição for inválida, o Spring retorna um erro 400 Bad Request automaticamente.
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
     * Endpoint para buscar todos os usuários.
     * URL: GET /usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios() {
        List<UsuarioResponseDTO> response = usuarioService.listarTodos();
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para atualizar um usuário existente.
     * URL: PUT /usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {

        UsuarioResponseDTO response = usuarioService.atualizar(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para deletar um usuário.
     * URL: DELETE /usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletar(id);
        // O padrão REST para uma operação DELETE bem-sucedida é retornar
        // o status 204 No Content, que não possui corpo de resposta.
        return ResponseEntity.noContent().build();
    }
}