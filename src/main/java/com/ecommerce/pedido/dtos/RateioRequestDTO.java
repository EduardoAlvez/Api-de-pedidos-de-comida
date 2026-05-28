package com.ecommerce.pedido.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateioRequestDTO {

    @NotNull(message = "O ID do produto é obrigatório.")
    private Long produtoId;

    @NotNull(message = "O valor pago é obrigatório.")
    @Positive(message = "O valor pago deve ser positivo.")
    private BigDecimal valorPago;
}
