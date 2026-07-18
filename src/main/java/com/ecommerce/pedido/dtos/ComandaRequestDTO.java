package com.ecommerce.pedido.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ComandaRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório.")
    private String clienteNome;

    @Valid
    private List<ComandaItemRequestDTO> itens;
}
