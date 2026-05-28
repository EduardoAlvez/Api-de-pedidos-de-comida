package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.ComandaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaItemRepository extends JpaRepository<ComandaItem, Long> {

    List<ComandaItem> findAllByComanda_Id(Long comandaId);
}
