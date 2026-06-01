package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.StatusTransacaoPix;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransacaoPix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String qrCodeBase64;

    @Column(columnDefinition = "TEXT")
    private String payloadCopiaCola;

    private String txId;

    @Enumerated(EnumType.STRING)
    private StatusTransacaoPix status;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;
}
