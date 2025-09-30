package com.ecommerce.pedido.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPedidoResponseDTO {
    private Long id;
    private Integer quantidade;
    private BigDecimal precoUnitario; //preço do produto no momento da compra
    private String nomeProduto;
    private Long produtoId;
}