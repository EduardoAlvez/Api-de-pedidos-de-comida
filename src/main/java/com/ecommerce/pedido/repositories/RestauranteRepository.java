package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}
