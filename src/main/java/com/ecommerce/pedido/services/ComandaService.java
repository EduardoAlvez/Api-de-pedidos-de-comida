package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.*;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusComanda;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.repositories.*;
import com.ecommerce.pedido.services.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ComandaRateioRepository comandaRateioRepository;
    private final ComandaItemRepository comandaItemRepository;

    public ComandaService(ComandaRepository comandaRepository, MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository,
                          ComandaRateioRepository comandaRateioRepository,
                          ComandaItemRepository comandaItemRepository) {
        this.comandaRepository = comandaRepository;
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.comandaRateioRepository = comandaRateioRepository;
        this.comandaItemRepository = comandaItemRepository;
    }

    @Transactional
    public ComandaResponseDTO criar(Long mesaId, Long garcomId, ComandaRequestDTO requestDTO) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada."));
        Usuario garcom = usuarioRepository.findById(garcomId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Garçom não encontrado."));

        Restaurante restauranteGarcom = garcom.getRestauranteVinculado();
        if (restauranteGarcom == null) {
            throw new ValidacaoNegocioException("Garcom nao vinculado a nenhum restaurante.");
        }
        if (!restauranteGarcom.getId().equals(mesa.getRestaurante().getId())) {
            throw new AcessoRestauranteException("Mesa nao pertence ao seu restaurante.");
        }

        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setGarcom(garcom);
        comanda.setClienteNome(requestDTO.getClienteNome());
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());

        List<ComandaItem> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        if (requestDTO.getItens() != null) {
            for (ComandaItemRequestDTO itemDTO : requestDTO.getItens()) {
                Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                        .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado."));

                ComandaItem item = new ComandaItem();
                item.setComanda(comanda);
                item.setProduto(produto);
                item.setQuantidade(itemDTO.getQuantidade());
                item.setPrecoUnitario(produto.getPreco());

                itens.add(item);
                valorTotal = valorTotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
            }
        }

        comanda.setItens(itens);
        comanda.setValorTotal(valorTotal);
        comanda.setRateios(new ArrayList<>());

        if (mesa.getStatus() == StatusMesa.LIVRE) {
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);
        }

        return toResponseDTO(comandaRepository.save(comanda));
    }

    @Transactional(readOnly = true)
    public List<ComandaResponseDTO> listarPorMesa(Long mesaId, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa nao encontrada."));
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(mesa.getRestaurante().getId())) {
            throw new EntidadeNaoEncontradaException("Comanda não encontrada.");
        }
        return comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(mesaId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComandaResponseDTO buscarPorId(Long id, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);
        return toResponseDTO(comanda);
    }

    @Transactional
    public ComandaItemResponseDTO adicionarItem(Long comandaId, ComandaItemRequestDTO requestDTO, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ValidacaoNegocioException("Comanda não está aberta.");
        }

        Produto produto = produtoRepository.findById(requestDTO.getProdutoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado."));

        ComandaItem item = new ComandaItem();
        item.setComanda(comanda);
        item.setProduto(produto);
        item.setQuantidade(requestDTO.getQuantidade());
        item.setPrecoUnitario(produto.getPreco());

        comanda.getItens().add(item);
        comanda.setValorTotal(comanda.getValorTotal().add(produto.getPreco().multiply(BigDecimal.valueOf(requestDTO.getQuantidade()))));
        ComandaItem savedItem = comandaItemRepository.save(item);
        comandaRepository.save(comanda);

        return toItemDTO(savedItem);
    }

    @Transactional
    public ComandaItemResponseDTO atualizarItem(Long comandaId, Long itemId, ComandaItemRequestDTO requestDTO, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ValidacaoNegocioException("Comanda não está aberta.");
        }

        ComandaItem item = comandaItemRepository.findById(itemId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Item não encontrado."));

        if (!item.getComanda().getId().equals(comandaId)) {
            throw new EntidadeNaoEncontradaException("Item não pertence a esta comanda.");
        }

        BigDecimal diferenca = BigDecimal.ZERO;
        if (requestDTO.getQuantidade() != null) {
            diferenca = item.getPrecoUnitario().multiply(
                    BigDecimal.valueOf(requestDTO.getQuantidade() - item.getQuantidade()));
            item.setQuantidade(requestDTO.getQuantidade());
        }

        comanda.setValorTotal(comanda.getValorTotal().add(diferenca));
        comandaItemRepository.save(item);
        comandaRepository.save(comanda);

        return toItemDTO(item);
    }

    @Transactional
    public void removerItem(Long comandaId, Long itemId, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ValidacaoNegocioException("Comanda não está aberta.");
        }

        ComandaItem item = comandaItemRepository.findById(itemId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Item não encontrado."));

        if (!item.getComanda().getId().equals(comandaId)) {
            throw new EntidadeNaoEncontradaException("Item não pertence a esta comanda.");
        }

        comanda.setValorTotal(comanda.getValorTotal().subtract(
                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))));
        comanda.getItens().remove(item);
        comandaItemRepository.delete(item);
        comandaRepository.save(comanda);
    }

    @Transactional
    public ComandaResponseDTO rateio(Long comandaId, RateioRequestDTO requestDTO, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        Produto produto = produtoRepository.findById(requestDTO.getProdutoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado."));

        Mesa mesa = comanda.getMesa();

        // Calcula quanto já foi pago deste produto na mesa
        List<Comanda> comandasDaMesa = comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(mesa.getId());
        BigDecimal totalPago = BigDecimal.ZERO;
        BigDecimal precoTotal = BigDecimal.ZERO;

        // Preco total vem dos itens compartilhados da mesa
        List<ItemCompartilhado> compartilhados = Optional.ofNullable(mesa.getItensCompartilhados())
                .orElse(Collections.emptyList());
        for (ItemCompartilhado ic : compartilhados) {
            if (ic.getProduto().getId().equals(produto.getId())) {
                precoTotal = precoTotal.add(ic.getPrecoUnitario().multiply(BigDecimal.valueOf(ic.getQuantidade())));
            }
        }

        // Total pago vem dos rateios já registrados
        for (Comanda c : comandasDaMesa) {
            for (ComandaRateio r : c.getRateios()) {
                if (r.getProduto().getId().equals(produto.getId())) {
                    totalPago = totalPago.add(r.getValorPago());
                }
            }
        }

        BigDecimal saldoRestante = precoTotal.subtract(totalPago);
        if (requestDTO.getValorPago().compareTo(saldoRestante) > 0) {
            throw new ValidacaoNegocioException(
                    "Valor do rateio excede o saldo pendente. Saldo restante: R$ " + saldoRestante);
        }

        ComandaRateio rateio = new ComandaRateio();
        rateio.setComanda(comanda);
        rateio.setProduto(produto);
        rateio.setValorPago(requestDTO.getValorPago());
        rateio.setDataPagamento(LocalDateTime.now());

        comanda.getRateios().add(rateio);
        comandaRateioRepository.save(rateio);

        return toResponseDTO(comandaRepository.save(comanda));
    }

    @Transactional
    public ComandaResponseDTO fechar(Long comandaId, FormaPagamento formaPagamento, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        if (comanda.getStatus() == StatusComanda.PAGA) {
            throw new ValidacaoNegocioException("Comanda já está paga.");
        }

        if (formaPagamento == FormaPagamento.PIX) {
            throw new ValidacaoNegocioException("Use o endpoint /pix para gerar QR Code.");
        }

        comanda.setStatus(StatusComanda.PAGA);
        comanda.setDataFechamento(LocalDateTime.now());
        comandaRepository.save(comanda);

        long abertasOuPendentes = comandaRepository.countByMesa_IdAndStatus(
                comanda.getMesa().getId(), StatusComanda.ABERTA);
        abertasOuPendentes += comandaRepository.countByMesa_IdAndStatus(
                comanda.getMesa().getId(), StatusComanda.AGUARDANDO_PIX);

        if (abertasOuPendentes == 0) {
            Mesa mesa = comanda.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);
        }

        return toResponseDTO(comanda);
    }

    private void validarComandaRestaurante(Comanda comanda, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(comanda.getMesa().getRestaurante().getId())) {
            throw new EntidadeNaoEncontradaException("Comanda nao encontrada.");
        }
    }

    private ComandaResponseDTO toResponseDTO(Comanda comanda) {
        ComandaResponseDTO response = new ComandaResponseDTO();
        response.setId(comanda.getId());

        if (comanda.getMesa() != null) {
            response.setMesaId(comanda.getMesa().getId());
            response.setMesaNomeCliente(comanda.getMesa().getNomeCliente());
        }
        if (comanda.getGarcom() != null) {
            response.setGarcomId(comanda.getGarcom().getId());
            response.setGarcomNome(comanda.getGarcom().getNome());
        }

        response.setClienteNome(comanda.getClienteNome());
        response.setStatus(comanda.getStatus());
        response.setDataAbertura(comanda.getDataAbertura());
        response.setDataFechamento(comanda.getDataFechamento());
        response.setValorTotal(comanda.getValorTotal());

        if (comanda.getItens() != null) {
            response.setItens(comanda.getItens().stream().map(this::toItemDTO).collect(Collectors.toList()));
        }
        if (comanda.getRateios() != null) {
            response.setRateios(comanda.getRateios().stream().map(this::toRateioDTO).collect(Collectors.toList()));
        }

        return response;
    }

    private ComandaItemResponseDTO toItemDTO(ComandaItem item) {
        ComandaItemResponseDTO dto = new ComandaItemResponseDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setNomeProduto(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
        return dto;
    }

    private ComandaRateioResponseDTO toRateioDTO(ComandaRateio rateio) {
        ComandaRateioResponseDTO dto = new ComandaRateioResponseDTO();
        dto.setId(rateio.getId());
        dto.setProdutoId(rateio.getProduto().getId());
        dto.setNomeProduto(rateio.getProduto().getNome());
        dto.setValorPago(rateio.getValorPago());
        dto.setDataPagamento(rateio.getDataPagamento());
        return dto;
    }
}
