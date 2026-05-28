package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.StatusComanda;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "garcom_id")
    private Usuario garcom;

    private String clienteNome;

    @Enumerated(EnumType.STRING)
    private StatusComanda status;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    private BigDecimal valorTotal;

    @JsonManagedReference
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaItem> itens;

    @JsonManagedReference
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaRateio> rateios;
}
