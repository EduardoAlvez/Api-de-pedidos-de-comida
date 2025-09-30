package com.ecommerce.pedido.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ManyToAny;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private String imageUrl;
    private boolean disponivel = true;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;
}
