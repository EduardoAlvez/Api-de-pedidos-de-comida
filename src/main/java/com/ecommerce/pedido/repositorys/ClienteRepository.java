package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.Cliente;
import com.ecommerce.pedido.models.Pedido;
import com.ecommerce.pedido.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Pedido> findByClienteId(Long clienteId);
    List<Produto> findByRestauranteId(Long restauranteId);

}
