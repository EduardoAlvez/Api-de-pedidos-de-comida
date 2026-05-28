package com.ecommerce.pedido.controllers;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Mesas")
class MesaControllerTest extends BaseControllerTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarMesa_retornar201() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nomeCliente": "Teste",
                            "restauranteId": 1
                        }
                        """)
                .when()
                .post("/API/V1/mesas")
                .then()
                .statusCode(201)
                .body("status", equalTo("LIVRE"))
                .body("nomeCliente", equalTo("Teste"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Consulta")
    void deveListarMesasPorRestaurante() {
        given()
                .header("Authorization", "Bearer " + tokenGarcom())
                .when()
                .get("/API/V1/mesas?restauranteId=1")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }
}
