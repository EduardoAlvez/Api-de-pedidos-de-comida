package com.ecommerce.pedido.dtos;

import com.ecommerce.pedido.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres.")
    private String nome;

    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "O telefone não pode estar em branco.")
    @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres.")
    private String telefone;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @NotNull(message = "O tipo de usuário (role) não pode ser nulo.")
    private Role tipo;
}