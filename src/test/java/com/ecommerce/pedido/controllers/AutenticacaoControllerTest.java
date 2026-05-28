package com.ecommerce.pedido.controllers;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Autenticação")
class AutenticacaoControllerTest extends BaseControllerTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Login")
    void deveFazerLogin_retornar200_comToken() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "garcom@email.com",
                            "senha": "123456"
                        }
                        """)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Login")
    void deveRetornar401_quandoSenhaInvalida() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "garcom@email.com",
                            "senha": "senha-errada"
                        }
                        """)
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }
}
