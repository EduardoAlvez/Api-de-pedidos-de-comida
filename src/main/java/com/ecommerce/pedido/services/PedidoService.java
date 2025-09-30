package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.*;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import com.ecommerce.pedido.models.enums.StatusPedido;
import com.ecommerce.pedido.repositories.*;
import com.ecommerce.pedido.services.exceptions.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, RestauranteRepository restauranteRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO requestDTO) {
        // --- 1. VALIDAÇÕES INICIAIS ---
        Restaurante restaurante = restauranteRepository.findById(requestDTO.getRestauranteId())
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante não encontrado."));

        Pedido pedido = new Pedido();

        // Se for um pedido de usuário' logado...
        if (requestDTO.getUsuarioId() != null) {
            Usuario cliente = usuarioRepository.findById(requestDTO.getUsuarioId())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário cliente não encontrado."));
            pedido.setUsuario(cliente);
        } else { // Se for um pedido de convidado, valida os campos de convidado
            if (requestDTO.getNomeUsuarioConvidado() == null || requestDTO.getEmailUsuarioConvidado() == null) {
                throw new CamposObrigatorioSemLoginException("Nome e Email do convidado são obrigatórios para pedidos sem login.");
            }
            pedido.setNomeUsuarioConvidado(requestDTO.getNomeUsuarioConvidado());
            pedido.setEmailUsuarioConvidado(requestDTO.getEmailUsuarioConvidado());
            pedido.setTelefoneUsuarioConvidado(requestDTO.getTelefoneUsuarioConvidado());
        }

        // --- 2. MONTAGEM DO PEDIDO E PROCESSAMENTO DOS ITENS ---
        pedido.setRestaurante(restaurante);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_CONFIRMACAO);
        pedido.setEnderecoDeEntrega(requestDTO.getEnderecoDeEntrega());
        pedido.setObservacoes(requestDTO.getObservacoes());

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO itemDTO : requestDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com ID " + itemDTO.getProdutoId() + " não encontrado."));

            // Garante que o produto pertence ao restaurante do pedido.
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new ValidacaoNegocioException("O produto '" + produto.getNome() + "' Não pertence a este restaurante.");
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido); //bidirecional
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco()); // Grava o preço do momento da compra

            itens.add(itemPedido);
            subtotal = subtotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
        }

        pedido.setItens(itens);

        // --- 3. CÁLCULO DE TOTAIS E CRIAÇÃO DO PAGAMENTO ---
        // A taxa de entrega poderia ser (uma lista de locais)
        BigDecimal taxaEntrega = new BigDecimal("5.00");

        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(subtotal.add(taxaEntrega));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValorTotal(pedido.getValorTotal());
        pagamento.setFormaDePagamento(requestDTO.getFormaDePagamento());
        pagamento.setStatus(StatusPagamento.PENDENTE); // sempre começar como pendente

        pedido.setPagamento(pagamento); // Associa o pagamento ao pedido

        // --- 4. PERSISTÊNCIA E FINALIZAÇÃO ---
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Gera um código após ter o "ID"
        pedidoSalvo.setCodigoPedido(String.format("PED-%06d", pedidoSalvo.getId()));
        pedidoRepository.save(pedidoSalvo);

        return toResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado com o ID: " + id));
        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorUsuario(Long usuarioId) {
        // Verificação
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com o ID: " + usuarioId);
        }

        List<Pedido> pedidos = pedidoRepository.findAllByUsuario_IdOrderByDataDoPedidoDesc(usuarioId);
        return pedidos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado com o ID: " + id));

        // Pensar numa lógica para validar a transição de status.
        pedido.setStatus(novoStatus);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return toResponseDTO(pedidoAtualizado);
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        BeanUtils.copyProperties(pedido, response);

        // Mapeamento dos DTOs aninhados
        PedidoResponseDTO.RestauranteSimpleDTO restDTO = new PedidoResponseDTO.RestauranteSimpleDTO();
        BeanUtils.copyProperties(pedido.getRestaurante(), restDTO);
        response.setRestaurante(restDTO);

        if (pedido.getUsuario() != null) {
            PedidoResponseDTO.UsuarioSimpleDTO userDTO = new PedidoResponseDTO.UsuarioSimpleDTO();
            BeanUtils.copyProperties(pedido.getUsuario(), userDTO);
            response.setCliente(userDTO);
        }

        response.setItens(pedido.getItens().stream()
                .map(this::toItemPedidoResponseDTO)
                .collect(Collectors.toList()));

        if (pedido.getPagamento() != null) {
            PagamentoResponseDTO pagamentoDTO = new PagamentoResponseDTO();
            BeanUtils.copyProperties(pedido.getPagamento(), pagamentoDTO);
            response.setPagamento(pagamentoDTO);
        }

        return response;
    }

    private ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido item) {
        ItemPedidoResponseDTO response = new ItemPedidoResponseDTO();
        BeanUtils.copyProperties(item, response);
        response.setNomeProduto(item.getProduto().getNome());
        response.setProdutoId(item.getProduto().getId());
        return response;
    }
}