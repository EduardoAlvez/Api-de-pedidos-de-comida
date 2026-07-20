package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.*;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.OrigemPedido;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.models.enums.StatusPagamento;
import com.ecommerce.pedido.models.enums.StatusPedido;
import com.ecommerce.pedido.models.enums.TamanhoPorcao;
import com.ecommerce.pedido.models.enums.TipoConsumo;
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
    private final RegiaoEntregaRepository regiaoEntregaRepository;

    public PedidoService(PedidoRepository pedidoRepository, RestauranteRepository restauranteRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, RegiaoEntregaRepository regiaoEntregaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.regiaoEntregaRepository = regiaoEntregaRepository;
    }

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO requestDTO) {
        // --- 1. VALIDAÇÕES INICIAIS ---
        Restaurante restaurante = restauranteRepository.findById(requestDTO.getRestauranteId())
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante não encontrado."));

        OrigemPedido origem = requestDTO.getOrigem();

        // Valida endereço de entrega para DELIVERY
        if (origem == OrigemPedido.DELIVERY) {
            if (requestDTO.getEnderecoDeEntrega() == null || requestDTO.getEnderecoDeEntrega().isBlank()) {
                throw new ValidacaoNegocioException("Endereço de entrega é obrigatório para pedidos DELIVERY.");
            }
        }

        // Define tipoConsumo padrão para PRESENCIAL
        TipoConsumo tipoConsumo = requestDTO.getTipoConsumo();
        if (origem == OrigemPedido.PRESENCIAL && tipoConsumo == null) {
            tipoConsumo = TipoConsumo.COMER_AQUI;
        }

        Pedido pedido = new Pedido();

        // Se for um pedido de usuário logado...
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
        pedido.setOrigem(origem);
        pedido.setTipoConsumo(tipoConsumo);
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

            TamanhoPorcao tamanho = itemDTO.getTamanho() != null ? itemDTO.getTamanho() : TamanhoPorcao.INTEIRA;

            BigDecimal precoUnitario;
            if (tamanho == TamanhoPorcao.MEIA) {
                if (produto.getPrecoMeia() == null) {
                    throw new ValidacaoNegocioException("O produto '" + produto.getNome() + "' não oferece meia porção.");
                }
                precoUnitario = produto.getPrecoMeia();
            } else {
                precoUnitario = produto.getPreco();
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(precoUnitario);
            itemPedido.setTamanho(tamanho);

            itens.add(itemPedido);
            subtotal = subtotal.add(precoUnitario.multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
        }

        pedido.setItens(itens);

        // --- 3. CÁLCULO DE TOTAIS E CRIAÇÃO DO PAGAMENTO ---
        BigDecimal taxaEntrega = BigDecimal.ZERO;
        if (requestDTO.getRegiaoEntregaId() != null) {
            RegiaoEntrega regiao = regiaoEntregaRepository.findById(requestDTO.getRegiaoEntregaId())
                    .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + requestDTO.getRegiaoEntregaId()));
            if (!regiao.getRestaurante().getId().equals(restaurante.getId())) {
                throw new ValidacaoNegocioException("A região de entrega não pertence a este restaurante.");
            }
            taxaEntrega = regiao.getValorFrete();
        }

        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(subtotal.add(taxaEntrega));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValorTotal(pedido.getValorTotal());
        pagamento.setFormaDePagamento(requestDTO.getFormaDePagamento());
        pagamento.setStatus(StatusPagamento.PENDENTE);

        pedido.setPagamento(pagamento);

        // --- 4. PERSISTÊNCIA E FINALIZAÇÃO ---
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

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
    public List<PedidoResponseDTO> listarPorUsuario(Long usuarioId, Usuario usuarioLogado) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com o ID: " + usuarioId);
        }

        List<Pedido> pedidos;
        if (usuarioLogado.getTipo() != Role.CLIENTE) {
            Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
            if (restauranteVinculado == null) {
                throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
            }
            pedidos = pedidoRepository.findAllByRestaurante_IdOrderByDataDoPedidoDesc(restauranteVinculado.getId());
        } else {
            pedidos = pedidoRepository.findAllByUsuario_IdOrderByDataDoPedidoDesc(usuarioId);
        }
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