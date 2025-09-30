package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String codigoPedido;
    private LocalDateTime dataDoPedido;
    private String observacoes;
    private String enderecoDeEntrega;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    // --- Valores explícitos e sem ambiguidade ---
    private BigDecimal subtotal;        // Valor dos itens
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;      // Valor final (subtotal + entrega)

    // --- Dados do Comprador Convidado ---
    private String nomeUsuarioConvidado;
    private String telefoneUsuarioConvidado;
    private String emailUsuarioConvidado;


    // --- Relacionamentos ---

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @JsonManagedReference
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;
}