package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PagamentoResponseDTO {

    private Long id;
    private StatusPagamento status;
    private FormaPagamento formaDePagamento;
    private BigDecimal valorTotal;
    private LocalDateTime dataDoPagamento; //nulo até confirmar
}