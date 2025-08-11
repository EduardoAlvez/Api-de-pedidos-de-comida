package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
