package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarStatusPedidoDTO {

    @NotNull(message = "O novo status não pode ser nulo.")
    private StatusPedido novoStatus;
}