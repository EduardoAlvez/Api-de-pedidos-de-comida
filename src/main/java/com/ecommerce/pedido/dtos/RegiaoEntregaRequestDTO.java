package com.ecommerce.pedido.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegiaoEntregaRequestDTO {

    @NotBlank(message = "O nome da região não pode estar em branco.")
    private String nome;

    @NotNull(message = "O valor do frete é obrigatório.")
    @PositiveOrZero(message = "O valor do frete deve ser positivo ou zero.")
    private BigDecimal valorFrete;
}
