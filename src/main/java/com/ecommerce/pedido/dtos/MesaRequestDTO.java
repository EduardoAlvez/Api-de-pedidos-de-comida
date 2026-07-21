package com.ecommerce.pedido.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaRequestDTO {

    @NotNull(message = "O ID do restaurante é obrigatório.")
    private Long restauranteId;

    @NotBlank(message = "O nome do cliente é obrigatório.")
    private String nomeCliente;

    private Integer numero;
}
