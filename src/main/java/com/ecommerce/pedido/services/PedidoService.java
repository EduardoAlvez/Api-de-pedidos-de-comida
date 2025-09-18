package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dto.ItemPedidoDTO;
import com.ecommerce.pedido.dto.PedidoDTO;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.repositories.ClienteRepository;
import com.ecommerce.pedido.repositories.PedidoRepository;
import com.ecommerce.pedido.repositories.ProdutoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public Pedido criarPedido(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        BeanUtils.copyProperties(dto, pedido);
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());

        double total = 0.0;

        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPedido(pedido);

            pedido.getItens().add(item);
            total += produto.getPreco().doubleValue() * itemDTO.getQuantidade();
        }

        pedido.setValorTotal(total);
        return pedidoRepository.save(pedido);
    }
}
