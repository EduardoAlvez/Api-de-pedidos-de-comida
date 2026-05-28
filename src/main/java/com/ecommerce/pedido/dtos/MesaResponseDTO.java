package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.StatusMesa;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MesaResponseDTO {

    private Long id;
    private String nomeCliente;
    private StatusMesa status;
    private LocalDateTime dataAbertura;
    private Long restauranteId;
    private String restauranteNome;
}
