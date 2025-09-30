package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    //pedidos de um usuario específico, ordenando pelos mais recentes.
    List<Pedido> findAllByUsuario_IdOrderByDataDoPedidoDesc(Long usuarioId);
}