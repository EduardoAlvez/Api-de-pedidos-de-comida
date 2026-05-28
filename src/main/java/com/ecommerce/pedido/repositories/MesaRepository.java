package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findAllByRestaurante_Id(Long restauranteId);

    boolean existsByRestaurante_IdAndStatus(Long restauranteId, com.ecommerce.pedido.models.enums.StatusMesa status);
}
