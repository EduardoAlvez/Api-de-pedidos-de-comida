package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    //Busca todos os produtos que pertencem a um restaurante específico, identificado pelo seu ID.
    List<Produto> findAllByRestaurante_Id(Long restauranteId);
}