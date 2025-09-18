package com.ecommerce.pedido.services;


import com.ecommerce.pedido.dto.PagamentoDTO;
import com.ecommerce.pedido.models.Pagamento;
import com.ecommerce.pedido.models.Pedido;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import com.ecommerce.pedido.repositories.PagamentoRepository;
import com.ecommerce.pedido.repositories.PedidoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Pagamento registrarPagamento(PagamentoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Pagamento pagamento = new Pagamento();
        BeanUtils.copyProperties(dto, pagamento);
        pagamento.setPedido(pedido);
        pagamento.setStatus(StatusPagamento.PENDENTE);

        return pagamentoRepository.save(pagamento);
    }
}

