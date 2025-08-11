package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
