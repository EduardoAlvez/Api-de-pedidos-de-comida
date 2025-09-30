package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role tipo;
}