package com.ecommerce.pedido.controllers;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Pedidos Delivery")
class PedidoControllerTest extends BaseControllerTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarPedidoDelivery_retornar201() {
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
                            "formaDePagamento": "PIX",
                            "origem": "DELIVERY"
                        }
                        """)
                .when()
                .post("/API/V1/pedidos")
                .then()
                .statusCode(201)
                .body("status", equalTo("AGUARDANDO_CONFIRMACAO"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Validação")
    void deveRetornar400_quandoSemEndereco() {
        given()
                .header("Authorization", "Bearer " + tokenCliente())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "restauranteId": 1,
                            "usuarioId": 1,
                            "itens": [{"produtoId": 1, "quantidade": 1}],
                            "formaDePagamento": "PIX",
                            "origem": "DELIVERY"
                        }
                        """)
                .when()
                .post("/API/V1/pedidos")
                .then()
                .statusCode(400);
    }
}
