package com.ecommerce.pedido.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

@Getter
@Setter
public class RestauranteRequestDTO {

    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 2, message = "O nome deve ter no mínimo 2 caracteres.")
    private String nome;

    @NotBlank(message = "O endereço não pode estar em branco.")
    private String endereco;

    @NotBlank(message = "O telefone não pode estar em branco.")
    private String telefone;

    @NotBlank(message = "O CNPJ não pode estar em branco.")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos.")
    private String cnpj;

    @NotBlank(message = "O tipo de cozinha não pode estar em branco.")
    private String tipoCozinha;

    // ID do usuário que será o dono deste restaurante.
    @NotNull(message = "O ID do usuário dono é obrigatório.")
    private Long usuarioId;
}