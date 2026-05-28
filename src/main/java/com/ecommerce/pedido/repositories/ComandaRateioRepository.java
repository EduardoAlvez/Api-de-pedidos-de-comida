package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.ComandaRateio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRateioRepository extends JpaRepository<ComandaRateio, Long> {

    List<ComandaRateio> findAllByComanda_Id(Long comandaId);
}
