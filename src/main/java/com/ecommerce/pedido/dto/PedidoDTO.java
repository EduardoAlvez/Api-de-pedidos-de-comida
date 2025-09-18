package com.ecommerce.pedido.dto;


import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    private Long id;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Restaurante é obrigatório")
    private Long restauranteId;

    private LocalDateTime dataHora;

    private List<ItemPedidoDTO> itens; // lista de itens do pedido

    private Double valorTotal;
}

