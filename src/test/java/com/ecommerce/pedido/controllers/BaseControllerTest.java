package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.services.token.TokenService;
import io.qameta.allure.Attachment;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    protected String tokenGarcom() {
        Usuario garcom = new Usuario();
        garcom.setId(4L);
        garcom.setEmail("garcom@email.com");
        garcom.setNome("Pedro Garçom");
        garcom.setTipo(Role.GARCOM);
        return tokenService.gerarToken(garcom);
    }

    protected String tokenCliente() {
        Usuario cliente = new Usuario();
        cliente.setId(1L);
        cliente.setEmail("cliente@email.com");
        cliente.setNome("Maria Cliente");
        cliente.setTipo(Role.CLIENTE);
        return tokenService.gerarToken(cliente);
    }

    // Token do dono do restaurante1 (Sabor Brasileiro)
    protected String tokenDono1() {
        Usuario dono = new Usuario();
        dono.setId(2L);
        dono.setEmail("dono1@email.com");
        dono.setNome("Maria Restaurante");
        dono.setTipo(Role.DONO_RESTAURANTE);
        return tokenService.gerarToken(dono);
    }

    // Token do dono do restaurante2 (Pizza Italiana) — usado em testes de isolação
    protected String tokenDono2() {
        Usuario dono = new Usuario();
        dono.setId(3L);
        dono.setEmail("dono2@email.com");
        dono.setNome("Carlos Restaurante");
        dono.setTipo(Role.DONO_RESTAURANTE);
        return tokenService.gerarToken(dono);
    }

    // Token de um GARCOM sem restaurante vinculado — usado em testes negativos
    protected String tokenGarcomSemVinculo() {
        Usuario garcom = new Usuario();
        garcom.setId(5L);
        garcom.setEmail("garcom2@email.com");
        garcom.setNome("Ana Garçom");
        garcom.setTipo(Role.GARCOM);
        return tokenService.gerarToken(garcom);
    }

    protected String headerAuth(String token) {
        return "Bearer " + token;
    }

    @Attachment(value = "{name}", type = "application/json")
    protected String attachmentPayload(String name, String content) {
        return content;
    }
}
