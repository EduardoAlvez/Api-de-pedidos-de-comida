package com.ecommerce.pedido.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RegiaoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    private BigDecimal valorFrete;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;
}
