package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.RegiaoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegiaoEntregaRepository extends JpaRepository<RegiaoEntrega, Long> {

    List<RegiaoEntrega> findAllByRestaurante_Id(Long restauranteId);
}
