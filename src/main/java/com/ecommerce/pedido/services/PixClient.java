package com.ecommerce.pedido.services;

import java.math.BigDecimal;

public interface PixClient {

    CriarCobrancaResult criarCobranca(BigDecimal valor, String descricao);

    record CriarCobrancaResult(
            String qrCodeBase64,
            String payloadCopiaCola,
            String txId
    ) {}
}
