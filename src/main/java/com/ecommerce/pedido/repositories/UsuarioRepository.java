package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Query
    Optional<Usuario> findByEmail(String email);
}