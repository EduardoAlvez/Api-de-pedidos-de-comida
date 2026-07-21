package com.ecommerce.pedido.services;

import com.ecommerce.pedido.configs.MercadoPagoConfigSetup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class MercadoPagoPixClient implements PixClient {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoPixClient.class);
    private static final String API_BASE = "https://api.mercadopago.com";

    private final MercadoPagoConfigSetup config;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public MercadoPagoPixClient(MercadoPagoConfigSetup config) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public CriarCobrancaResult criarCobranca(BigDecimal valor, String descricao) {
        String accessToken = config.getAccessToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("MP_ACCESS_TOKEN não configurado para este restaurante.");
        }

        try {
            String valorStr = valor.setScale(2, RoundingMode.HALF_UP).toString();

            ObjectNode body = mapper.createObjectNode();
            body.put("transaction_amount", valor);
            body.put("description", descricao);
            body.put("payment_method_id", "pix");
            body.put("installments", 1);

            ObjectNode payer = mapper.createObjectNode();
            payer.put("email", "comprador@email.com");
            body.set("payer", payer);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/v1/payments"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            log.info("Chamando API Mercado Pago: POST /v1/payments valor={}", valorStr);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            log.info("Resposta MP: HTTP {} - {}", response.statusCode(),
                    responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody);

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode json = mapper.readTree(responseBody);
                JsonNode txData = json.path("point_of_interaction").path("transaction_data");

                String txId = json.has("id") ? json.get("id").asText() : descricao;
                String qrData = txData.has("qr_code") ? txData.get("qr_code").asText() : null;
                String qrCodeBase64 = txData.has("qr_code_base64")
                        ? txData.get("qr_code_base64").asText() : null;

                log.info("PIX gerado: txId={}, qrData.length={}, qrCodeBase64.length={}",
                        txId, qrData != null ? qrData.length() : 0,
                        qrCodeBase64 != null ? qrCodeBase64.length() : 0);

                return new CriarCobrancaResult(qrCodeBase64, qrData, txId);
            }

            String errorMsg = "Erro na API do Mercado Pago: HTTP " + response.statusCode()
                    + " - " + responseBody;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao integrar com Mercado Pago", e);
            throw new RuntimeException("Erro ao integrar com Mercado Pago", e);
        }
    }
}
