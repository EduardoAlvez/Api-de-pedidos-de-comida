package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.StatusComanda;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ComandaResponseDTO {

    private Long id;
    private Long mesaId;
    private String mesaNomeCliente;
    private Long garcomId;
    private String garcomNome;
    private String clienteNome;
    private StatusComanda status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal valorTotal;
    private List<ComandaItemResponseDTO> itens;
    private List<ComandaRateioResponseDTO> rateios;
}
