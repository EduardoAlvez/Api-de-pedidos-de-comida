package com.ecommerce.pedido.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegiaoEntregaResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal valorFrete;
}
