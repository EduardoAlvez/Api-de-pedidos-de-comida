package com.ecommerce.pedido.models;

import com.ecommerce.pedido.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role tipo;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Restaurante restaurante;

    // ... métodos UserDetails (mantenha como estão)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipo == Role.DONO_RESTAURANTE) {
            return List.of(
                    new SimpleGrantedAuthority(Role.DONO_RESTAURANTE.getRole()),
                    new SimpleGrantedAuthority(Role.CLIENTE.getRole())
            );
        } else {
            return List.of(new SimpleGrantedAuthority(Role.CLIENTE.getRole()));
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}