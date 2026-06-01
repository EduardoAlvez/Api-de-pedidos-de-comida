package com.ecommerce.pedido.services;

import com.ecommerce.pedido.configs.MercadoPagoConfigSetup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;

@Service
public class MercadoPagoPixClient implements PixClient {

    private static final String POS_EXTERNAL_ID = "LOJ001POS001";
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
        try {
            String valorStr = valor.setScale(2, RoundingMode.HALF_UP).toString();

            ObjectNode body = mapper.createObjectNode();
            body.put("type", "qr");
            body.put("total_amount", valorStr);
            body.put("description", descricao);
            body.put("external_reference", descricao);

            ObjectNode configNode = mapper.createObjectNode();
            ObjectNode qrConfig = mapper.createObjectNode();
            qrConfig.put("external_pos_id", POS_EXTERNAL_ID);
            qrConfig.put("mode", "dynamic");
            configNode.set("qr", qrConfig);
            body.set("config", configNode);

            ObjectNode transactions = mapper.createObjectNode();
            ArrayNode payments = mapper.createArrayNode();
            ObjectNode payment = mapper.createObjectNode();
            payment.put("amount", valorStr);
            payments.add(payment);
            transactions.set("payments", payments);
            body.set("transactions", transactions);

            ArrayNode items = mapper.createArrayNode();
            ObjectNode item = mapper.createObjectNode();
            item.put("title", descricao);
            item.put("unit_price", valorStr);
            item.put("unit_measure", "unit");
            item.put("external_code", descricao);
            item.put("quantity", 1);
            items.add(item);
            body.set("items", items);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/v1/orders"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getAccessToken())
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode json = mapper.readTree(response.body());

                String txId = json.has("id") ? json.get("id").asText() : descricao;
                String qrData = json.path("type_response").has("qr_data")
                        ? json.path("type_response").get("qr_data").asText() : null;

                String qrCodeBase64 = null;
                if (qrData != null) {
                    qrCodeBase64 = gerarQrCodeBase64(qrData);
                }

                return new CriarCobrancaResult(qrCodeBase64, qrData, txId);
            }

            throw new RuntimeException("Erro na API do Mercado Pago: HTTP " + response.statusCode()
                    + " - " + response.body());

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao integrar com Mercado Pago", e);
        }
    }

    private String gerarQrCodeBase64(String payload) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
