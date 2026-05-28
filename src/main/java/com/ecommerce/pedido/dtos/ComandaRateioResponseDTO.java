package com.ecommerce.pedido.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ComandaRateioResponseDTO {

    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private BigDecimal valorPago;
    private LocalDateTime dataPagamento;
}
