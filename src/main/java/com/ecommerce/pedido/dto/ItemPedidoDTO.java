package com.ecommerce.pedido.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemPedidoDTO {

    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    @Min(value = 1, message = "Quantidade mínima é 1")
    private int quantidade;
}
