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
    private String cnpj;
    private String tipoCozinha;
    private String horarioFuncionamento;
    private String imageUrl;

    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario; // cada restaurante pertence a 1 cliente


}
