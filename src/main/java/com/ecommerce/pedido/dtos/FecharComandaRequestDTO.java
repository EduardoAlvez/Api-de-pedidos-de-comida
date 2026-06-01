package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FecharComandaRequestDTO {

    @NotNull(message = "A forma de pagamento é obrigatória.")
    private FormaPagamento formaPagamento;
}
