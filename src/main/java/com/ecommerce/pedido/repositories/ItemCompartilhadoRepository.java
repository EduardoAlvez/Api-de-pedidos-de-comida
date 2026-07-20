package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.ItemCompartilhado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCompartilhadoRepository extends JpaRepository<ItemCompartilhado, Long> {

    List<ItemCompartilhado> findAllByMesa_Id(Long mesaId);

    void deleteAllByMesa_Id(Long mesaId);
}
