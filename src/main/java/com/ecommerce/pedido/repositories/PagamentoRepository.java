package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
