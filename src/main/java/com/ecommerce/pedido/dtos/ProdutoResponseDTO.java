package com.ecommerce.pedido.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;

    // Restaurante do produto pertence,
    private RestauranteSimpleDTO restaurante;

    @Getter
    @Setter
    public static class RestauranteSimpleDTO {
        private Long id;
        private String nome;
    }
}