package com.ecommerce.pedido.dto;


import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {

    private Long id;

    @NotNull(message = "Pedido é obrigatório")
    private Long pedidoId;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private FormaPagamento formaPagamento;

    private StatusPagamento status;
}

