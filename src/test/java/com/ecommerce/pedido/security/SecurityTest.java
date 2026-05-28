package com.ecommerce.pedido.security;

import com.ecommerce.pedido.controllers.BaseControllerTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Segurança")
class SecurityTest extends BaseControllerTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Comandas")
    void criarComanda_comoGarcom_deveRetornar201() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Security",
                            "itens": [{"produtoId": 1, "quantidade": 1, "compartilhado": false}]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(201);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Comandas")
    void criarComanda_semToken_deveRetornar403() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Security",
                            "itens": [{"produtoId": 1, "quantidade": 1, "compartilhado": false}]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(403);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Mesas")
    void criarMesa_comoGarcom_deveRetornar201() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nomeCliente": "Security",
                            "restauranteId": 1
                        }
                        """)
                .when()
                .post("/API/V1/mesas")
                .then()
                .statusCode(201);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Pedidos")
    void criarPedido_comoCliente_deveRetornar201() {
        given()
                .header("Authorization", "Bearer " + tokenCliente())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "restauranteId": 1,
                            "usuarioId": 1,
                            "itens": [{"produtoId": 1, "quantidade": 1}],
                            "enderecoDeEntrega": "Rua Teste, 123",
                            "regiaoEntregaId": 1,
                            "formaDePagamento": "PIX"
                        }
                        """)
                .when()
                .post("/API/V1/pedidos")
                .then()
                .statusCode(201);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Token")
    void requisicaoComTokenInvalido_deveRetornar403() {
        given()
                .header("Authorization", "Bearer token-invalido-qualquer")
                .when()
                .get("/API/V1/comandas?mesaId=1")
                .then()
                .statusCode(403);
    }
}
