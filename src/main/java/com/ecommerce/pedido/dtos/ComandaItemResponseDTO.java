package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.TamanhoPorcao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComandaItemResponseDTO {

    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private TamanhoPorcao tamanho;
}
