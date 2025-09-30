package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private BigDecimal valorTotal;
    private LocalDateTime dataDoPagamento;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaDePagamento;
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
