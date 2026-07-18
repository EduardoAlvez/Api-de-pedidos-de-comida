package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.FecharComandaRequestDTO;
import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.services.PixClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Feature("Pix")
@Story("Controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PixControllerTest extends BaseControllerTest {

    @MockBean
    private PixClient pixClient;

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    void deveGerarQrCodePix() {
        when(pixClient.criarCobranca(any(BigDecimal.class), any(String.class)))
                .thenReturn(new PixClient.CriarCobrancaResult(
                        "iVBORw0KGgoAAAANSUhEUgAA...",
                        "pix-copia-cola-exemplo",
                        "TX-TEST-1"
                ));

        String token = tokenGarcom();

        given()
            .header("Authorization", headerAuth(token))
            .when()
            .post("/API/V1/comandas/1/pix")
            .then()
            .statusCode(201)
            .body("txId", notNullValue())
            .body("qrCodeBase64", notNullValue())
            .body("payloadCopiaCola", notNullValue())
            .body("status", equalTo("AGUARDANDO"));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    void deveConsultarStatusPix() {
        String token = tokenGarcom();

        given()
            .header("Authorization", headerAuth(token))
            .when()
            .get("/API/V1/comandas/1/pix")
            .then()
            .statusCode(200)
            .body("status", anyOf(equalTo("AGUARDANDO"), equalTo("CONFIRMADO")));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    void deveFecharComandaComMaquininha() {
        String token = tokenGarcom();

        FecharComandaRequestDTO dto = new FecharComandaRequestDTO();
        dto.setFormaPagamento(FormaPagamento.MAQUININHA);

        given()
            .header("Authorization", headerAuth(token))
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/API/V1/comandas/1/fechar")
            .then()
            .statusCode(200);
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.MINOR)
    void naoDeveGerarQrCode_paraComandaPaga() {
        String token = tokenGarcom();

        FecharComandaRequestDTO dto = new FecharComandaRequestDTO();
        dto.setFormaPagamento(FormaPagamento.MAQUININHA);

        given()
            .header("Authorization", headerAuth(token))
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/API/V1/comandas/2/fechar")
            .then()
            .statusCode(200);

        given()
            .header("Authorization", headerAuth(token))
            .when()
            .post("/API/V1/comandas/2/pix")
            .then()
            .statusCode(400);
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    void deveRetornar401_quandoWebhookComAssinaturaInvalida() {
        String body = """
                {
                    "action": "order.processed",
                    "data": { "id": "ORD999" }
                }
                """;

        given()
            .header("x-signature", "ts=123,v1=hash-invalido")
            .header("x-request-id", "req-test")
            .queryParam("data.id", "ORD999")
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/API/V1/pix/webhook")
            .then()
            .statusCode(401);
    }
}
