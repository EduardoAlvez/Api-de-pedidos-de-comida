package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoResponseDTO {

    private Long id;
    private String codigoPedido;
    private LocalDateTime dataDoPedido;
    private StatusPedido status;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String enderecoDeEntrega;
    private String observacoes;

    private RestauranteSimpleDTO restaurante;
    private UsuarioSimpleDTO cliente; //ode ser nulo se for convidado
    private List<ItemPedidoResponseDTO> itens;
    private PagamentoResponseDTO pagamento;

    // --- DTOs Aninhados ---
    @Getter
    @Setter
    public static class RestauranteSimpleDTO {
        private Long id;
        private String nome;
    }

    @Getter
    @Setter
    public static class UsuarioSimpleDTO {
        private Long id;
        private String nome;
    }


}