package com.ecommerce.pedido.repositories;

import com.ecommerce.pedido.models.TransacaoPix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoPixRepository extends JpaRepository<TransacaoPix, Long> {
    List<TransacaoPix> findAllByComanda_Id(Long comandaId);
    Optional<TransacaoPix> findByTxId(String txId);
}
