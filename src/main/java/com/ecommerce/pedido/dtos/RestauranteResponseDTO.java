package com.ecommerce.pedido.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class RestauranteResponseDTO {

    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private String cnpj;
    private String tipoCozinha;

    // Informação do dono. Não devemos expor todos os dados do usuário,
    // apenas o essencial, então criamos um DTO aninhado.
    private DonoRestauranteDTO dono;

    // DTO aninhado (inner DTO) para representar o dono de forma simplificada.
    @Getter
    @Setter
    public static class DonoRestauranteDTO {
        private Long id;
        private String nome;
    }
}