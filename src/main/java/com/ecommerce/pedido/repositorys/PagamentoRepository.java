package com.ecommerce.pedido.repositorys;

import com.ecommerce.pedido.models.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
