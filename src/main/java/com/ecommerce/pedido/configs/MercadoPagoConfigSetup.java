package com.ecommerce.pedido.configs;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfigSetup {

    @Value("${mercado-pago.access-token}")
    private String accessToken;

    @Value("${mercado-pago.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}
