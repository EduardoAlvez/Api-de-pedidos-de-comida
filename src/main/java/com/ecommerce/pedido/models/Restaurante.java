package com.ecommerce.pedido.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private String endereco;
    private String telefone;
    private String cnpj;
    private String tipoCozinha;
    private String horarioFuncionamento;
    private String imageUrl;

    @JsonManagedReference
    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;

    @JsonManagedReference
    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario; // cada restaurante pertence a 1 usuario


}
