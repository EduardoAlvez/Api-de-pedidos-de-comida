package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
