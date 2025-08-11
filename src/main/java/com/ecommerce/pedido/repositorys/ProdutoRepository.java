package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
