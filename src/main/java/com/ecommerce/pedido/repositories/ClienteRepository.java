package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
