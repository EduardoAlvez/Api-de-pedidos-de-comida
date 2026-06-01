package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.StatusTransacaoPix;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PixResponseDTO {

    private Long id;
    private Long comandaId;
    private BigDecimal valor;
    private String qrCodeBase64;
    private String payloadCopiaCola;
    private String txId;
    private StatusTransacaoPix status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;
}
