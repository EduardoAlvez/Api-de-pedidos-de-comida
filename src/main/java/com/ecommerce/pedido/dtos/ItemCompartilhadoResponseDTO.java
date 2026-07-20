package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.TamanhoPorcao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemCompartilhadoResponseDTO {

    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private String observacao;
    private TamanhoPorcao tamanho;
}
