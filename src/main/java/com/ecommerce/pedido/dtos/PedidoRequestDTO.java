package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidoRequestDTO {

    @NotNull(message = "O ID do restaurante é obrigatório.")
    private Long restauranteId;

    @NotNull(message = "A forma de pagamento é obrigatória.")
    private FormaPagamento formaDePagamento;

    // Se o pedido for feito por um usuário logado, este ID será preenchido.
    // Se for um convidado, será nulo.
    private Long usuarioId;

    // Se for um pedido de convidado (usuarioId nulo), estes campos são obrigatórios.
    private String nomeUsuarioConvidado;
    private String telefoneUsuarioConvidado;
    private String emailUsuarioConvidado;

    @NotEmpty(message = "O endereço de entrega não pode estar vazio.")
    private String enderecoDeEntrega;

    private String observacoes;

    @Valid // Valida os objetos dentro da lista
    @NotEmpty(message = "A lista de itens não pode estar vazia.")
    private List<ItemPedidoRequestDTO> itens;
}