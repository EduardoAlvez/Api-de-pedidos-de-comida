package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role tipo;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Restaurante restaurante; // dono do restaurante terá 1 restaurante

}
