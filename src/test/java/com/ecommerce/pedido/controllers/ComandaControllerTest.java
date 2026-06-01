package com.ecommerce.pedido.controllers;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Comandas")
class ComandaControllerTest extends BaseControllerTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarComanda_retornar201() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Joao",
                            "itens": [
                                {"produtoId": 1, "quantidade": 1, "compartilhado": false}
                            ]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(201)
                .body("status", equalTo("ABERTA"))
                .body("clienteNome", equalTo("Joao"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Criação")
    void deveRetornar404_quandoProdutoInexistente() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Joao",
                            "itens": [
                                {"produtoId": 999, "quantidade": 1, "compartilhado": false}
                            ]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Consulta")
    void deveListarComandasPorMesa() {
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
    @Story("Fluxo completo")
    @Description("Cria comanda com item compartilhado, faz rateio parcial e fecha a comanda")
    void deveCriarComanda_eFazerRateio_eFechar() {
        Integer comandaId = Allure.step("Criar comanda", () ->
            given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Maria",
                            "itens": [
                                {"produtoId": 1, "quantidade": 1, "compartilhado": false},
                                {"produtoId": 3, "quantidade": 1, "compartilhado": true}
                            ]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(201)
                .extract().path("id")
        );

        Allure.step("Fazer rateio do item compartilhado", () -> {
            given()
                    .header("Authorization", "Bearer " + tokenGarcom())
                    .contentType(ContentType.JSON)
                    .body("""
                            { "produtoId": 3, "valorPago": 5.00 }
                            """)
                    .when()
                    .post("/API/V1/comandas/" + comandaId + "/rateio")
                    .then()
                    .statusCode(200)
                    .body("rateios", hasSize(1));
        });

        Allure.step("Fechar comanda", () -> {
            given()
                    .header("Authorization", "Bearer " + tokenGarcom())
                    .contentType(ContentType.JSON)
                    .body("""
                            { "formaPagamento": "MAQUININHA" }
                            """)
                    .when()
                    .post("/API/V1/comandas/" + comandaId + "/fechar")
                    .then()
                    .statusCode(200)
                    .body("status", equalTo("PAGA"));
        });
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Rateio")
    void deveRejeitarRateioComValorExcessivo() {
        Integer comandaId = Allure.step("Criar comanda com item compartilhado", () ->
            given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "clienteNome": "Pedro",
                            "itens": [
                                {"produtoId": 3, "quantidade": 1, "compartilhado": true}
                            ]
                        }
                        """)
                .when()
                .post("/API/V1/mesas/1/comandas")
                .then()
                .statusCode(201)
                .extract().path("id")
        );

        Allure.step("Tentar rateio com valor maior que o saldo", () -> {
            given()
                    .header("Authorization", "Bearer " + tokenGarcom())
                    .contentType(ContentType.JSON)
                    .body("""
                            { "produtoId": 3, "valorPago": 999.00 }
                            """)
                    .when()
                    .post("/API/V1/comandas/" + comandaId + "/rateio")
                    .then()
                    .statusCode(400);
        });
    }
}
