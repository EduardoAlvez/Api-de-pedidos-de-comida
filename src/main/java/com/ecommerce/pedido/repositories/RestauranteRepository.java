package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Buscar um restaurante pelo CNPJ
    Optional<Restaurante> findByCnpj(String cnpj);

    // Verificar se já existe um restaurante associado a um ID de usuário (cliente/dono)
    boolean existsByUsuario_Id(Long usuarioId);
}