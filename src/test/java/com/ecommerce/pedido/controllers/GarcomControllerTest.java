package com.ecommerce.pedido.controllers;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Vincular Garçom")
@Story("Controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GarcomControllerTest extends BaseControllerTest {

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    void deveVincularGarcom_donoRetornar200() {
        // Dono1 vincula garcom2 (sem vinculo) ao restaurante1
        given()
            .header("Authorization", "Bearer " + tokenDono1())
            .contentType(ContentType.JSON)
            .body("""
                { "usuarioId": 5 }
                """)
        .when()
            .post("/API/V1/restaurantes/1/garcons/vincular")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    void deveRetornar403_quandoClienteTentaVincular() {
        given()
            .header("Authorization", "Bearer " + tokenCliente())
            .contentType(ContentType.JSON)
            .body("""
                { "usuarioId": 5 }
                """)
        .when()
            .post("/API/V1/restaurantes/1/garcons/vincular")
        .then()
            .statusCode(403);
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    void deveRetornar403_quandoDonoOutroRestauranteTentaVincular() {
        // Dono2 tenta vincular ao restaurante1 (que nao e seu)
        given()
            .header("Authorization", "Bearer " + tokenDono2())
            .contentType(ContentType.JSON)
            .body("""
                { "usuarioId": 5 }
                """)
        .when()
            .post("/API/V1/restaurantes/1/garcons/vincular")
        .then()
            .statusCode(403);
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    void deveListarGarcons_retornar200() {
        given()
            .header("Authorization", "Bearer " + tokenDono1())
        .when()
            .get("/API/V1/restaurantes/1/garcons")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.BLOCKER)
    void deveDesvincularGarcom_donoRetornar204() {
        given()
            .header("Authorization", "Bearer " + tokenDono1())
        .when()
            .delete("/API/V1/restaurantes/1/garcons/5")
        .then()
            .statusCode(204);
    }
}
