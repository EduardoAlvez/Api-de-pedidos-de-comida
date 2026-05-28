package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findAllByMesa_IdOrderByDataAberturaDesc(Long mesaId);

    long countByMesa_IdAndStatus(Long mesaId, com.ecommerce.pedido.models.enums.StatusComanda status);
}
