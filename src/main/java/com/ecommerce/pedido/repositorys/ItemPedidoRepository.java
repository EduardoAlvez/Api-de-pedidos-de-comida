package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
