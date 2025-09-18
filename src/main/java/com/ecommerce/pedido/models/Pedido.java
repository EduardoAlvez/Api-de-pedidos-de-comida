package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.StatusPagamento;
import com.ecommerce.pedido.models.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime dataDoPedido;
    private StatusPedido Status;
    private Double valorTotal;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToOne(mappedBy = "pedido")
    private  Pagamento pagamento;

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> itens;

}
