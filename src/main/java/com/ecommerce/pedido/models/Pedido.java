package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;
}