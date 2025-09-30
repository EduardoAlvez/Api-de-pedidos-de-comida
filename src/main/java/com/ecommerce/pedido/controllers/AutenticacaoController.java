package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.LoginRequestDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.services.token.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoController(TokenService tokenService, AuthenticationManager manager) {
        this.tokenService = tokenService;
        this.manager = manager;

    }

    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid LoginRequestDTO dados) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var authentication = manager.authenticate(authToken);

            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

            return ResponseEntity.ok(Map.of("token", tokenJWT, "type", "Bearer"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno no servidor"));
        }
    }
}