package com.ecommerce.pedido.dto;

import com.ecommerce.pedido.models.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "E-mail inválido")
    private String email;

    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha; // pode ser null se for CLIENTE comum
    @NotBlank(message = "RESTAURANTE ou CLIENTE ")
    private Role role;
}
