package com.ecommerce.pedido.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;

    @OneToMany(mappedBy = "restaurante")
    @JoinColumn(name = "produtos_id")
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    @JoinColumn(name = "pedidos_id")
    private List<Pedido> pedidos;

}
