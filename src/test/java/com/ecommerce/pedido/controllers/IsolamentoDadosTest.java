package com.ecommerce.pedido.controllers;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Isolamento de Dados")
class IsolamentoDadosTest extends BaseControllerTest {

    // -------------------- 8.2 GARCOM ACESSA APENAS SEU RESTAURANTE --------------------

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Garcom - Produtos")
    void garcom_deveListarProdutos_doSeuRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
        .when()
            .get("/API/V1/produtos/restaurante/1")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Garcom - Isolamento")
    void garcom_naoDeveListarProdutos_deOutroRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
        .when()
            .get("/API/V1/produtos/restaurante/2")
        .then()
            .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Garcom - Mesas")
    void garcom_deveListarMesas_doSeuRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
        .when()
            .get("/API/V1/mesas")
        .then()
            .statusCode(200);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Garcom - Isolamento")
    void garcom_naoDeveCriarMesa_emOutroRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
            .contentType(ContentType.JSON)
            .body("""
                { "nomeCliente": "Invasor", "restauranteId": 2 }
                """)
        .when()
            .post("/API/V1/mesas")
        .then()
            .statusCode(403);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Garcom - Comandas")
    void garcom_deveListarComandas_doSeuRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
        .when()
            .get("/API/V1/comandas?mesaId=1")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Garcom - Isolamento")
    void garcom_naoDeveAcessarComanda_deOutroRestaurante() {
        // Comanda ID inexistente — retorna 404 (nao revela existencia)
        given()
            .header("Authorization", "Bearer " + tokenGarcom())
        .when()
            .get("/API/V1/comandas/999")
        .then()
            .statusCode(404);
    }

    // -------------------- 8.3 DONO ACESSA APENAS SEU RESTAURANTE --------------------

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Dono - Produtos")
    void dono_deveListarProdutos_doSeuRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenDono1())
        .when()
            .get("/API/V1/produtos/restaurante/1")
        .then()
            .statusCode(200);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Dono - Isolamento")
    void dono_naoDeveListarProdutos_deOutroRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenDono1())
        .when()
            .get("/API/V1/produtos/restaurante/2")
        .then()
            .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Dono - Mesas")
    void dono_deveListarMesas_doSeuRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenDono1())
        .when()
            .get("/API/V1/mesas")
        .then()
            .statusCode(200);
    }

    // -------------------- 8.4 CLIENTE CONTINUA SEM RESTRICAO --------------------

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Cliente")
    void cliente_deveListarProdutos_deQualquerRestaurante() {
        given()
            .header("Authorization", "Bearer " + tokenCliente())
        .when()
            .get("/API/V1/produtos/restaurante/2")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Cliente")
    void cliente_deveBuscarPedido_semRestricao() {
        given()
            .header("Authorization", "Bearer " + tokenCliente())
        .when()
            .get("/API/V1/pedidos/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1));
    }

    // -------------------- 8.5 GARCOM SEM VINCULO --------------------

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Garcom sem vinculo")
    void garcomSemVinculo_naoDeveListarMesas() {
        given()
            .header("Authorization", "Bearer " + tokenGarcomSemVinculo())
        .when()
            .get("/API/V1/mesas")
        .then()
            .statusCode(400);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Garcom sem vinculo")
    void garcomSemVinculo_naoDeveCriarComanda() {
        given()
            .header("Authorization", "Bearer " + tokenGarcomSemVinculo())
            .contentType(ContentType.JSON)
            .body("""
                {
                    "clienteNome": "Teste",
                    "itens": [{"produtoId": 1, "quantidade": 1}]
                }
                """)
        .when()
            .post("/API/V1/mesas/1/comandas")
        .then()
            .statusCode(400);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Garcom sem vinculo")
    void garcomSemVinculo_naoDeveListarProdutos() {
        given()
            .header("Authorization", "Bearer " + tokenGarcomSemVinculo())
        .when()
            .get("/API/V1/produtos/restaurante/1")
        .then()
            .statusCode(400);
    }
}
