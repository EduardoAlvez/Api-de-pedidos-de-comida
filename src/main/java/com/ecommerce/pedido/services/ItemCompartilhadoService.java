package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.ItemCompartilhadoRequestDTO;
import com.ecommerce.pedido.dtos.ItemCompartilhadoResponseDTO;
import com.ecommerce.pedido.models.ItemCompartilhado;
import com.ecommerce.pedido.models.Mesa;
import com.ecommerce.pedido.models.Produto;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.TamanhoPorcao;
import com.ecommerce.pedido.repositories.ItemCompartilhadoRepository;
import com.ecommerce.pedido.repositories.MesaRepository;
import com.ecommerce.pedido.repositories.ProdutoRepository;
import com.ecommerce.pedido.services.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemCompartilhadoService {

    private final ItemCompartilhadoRepository itemCompartilhadoRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRepository produtoRepository;

    public ItemCompartilhadoService(ItemCompartilhadoRepository itemCompartilhadoRepository,
                                    MesaRepository mesaRepository,
                                    ProdutoRepository produtoRepository) {
        this.itemCompartilhadoRepository = itemCompartilhadoRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public ItemCompartilhadoResponseDTO adicionar(Long mesaId, ItemCompartilhadoRequestDTO requestDTO, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa nao encontrada."));
        validarAcessoMesa(mesa, usuarioLogado);

        Produto produto = produtoRepository.findById(requestDTO.getProdutoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto nao encontrado."));

        TamanhoPorcao tamanho = requestDTO.getTamanho() != null ? requestDTO.getTamanho() : TamanhoPorcao.INTEIRA;
        BigDecimal precoUnitario = obterPreco(produto, tamanho);

        ItemCompartilhado item = new ItemCompartilhado();
        item.setMesa(mesa);
        item.setProduto(produto);
        item.setQuantidade(requestDTO.getQuantidade());
        item.setPrecoUnitario(precoUnitario);
        item.setObservacao(requestDTO.getObservacao());
        item.setTamanho(tamanho);

        return toResponseDTO(itemCompartilhadoRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<ItemCompartilhadoResponseDTO> listar(Long mesaId, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa nao encontrada."));
        validarAcessoMesa(mesa, usuarioLogado);

        return itemCompartilhadoRepository.findAllByMesa_Id(mesaId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemCompartilhadoResponseDTO atualizar(Long mesaId, Long itemId, ItemCompartilhadoRequestDTO requestDTO, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa nao encontrada."));
        validarAcessoMesa(mesa, usuarioLogado);

        ItemCompartilhado item = itemCompartilhadoRepository.findById(itemId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Item compartilhado nao encontrado."));

        if (!item.getMesa().getId().equals(mesaId)) {
            throw new EntidadeNaoEncontradaException("Item compartilhado nao pertence a essa mesa.");
        }

        if (requestDTO.getProdutoId() != null && !requestDTO.getProdutoId().equals(item.getProduto().getId())) {
            Produto produto = produtoRepository.findById(requestDTO.getProdutoId())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto nao encontrado."));
            item.setProduto(produto);
            TamanhoPorcao tamanho = requestDTO.getTamanho() != null ? requestDTO.getTamanho() : item.getTamanho();
            item.setPrecoUnitario(obterPreco(produto, tamanho));
            item.setTamanho(tamanho);
        } else if (requestDTO.getTamanho() != null && !requestDTO.getTamanho().equals(item.getTamanho())) {
            item.setTamanho(requestDTO.getTamanho());
            item.setPrecoUnitario(obterPreco(item.getProduto(), requestDTO.getTamanho()));
        }

        if (requestDTO.getQuantidade() != null) {
            item.setQuantidade(requestDTO.getQuantidade());
        }
        item.setObservacao(requestDTO.getObservacao());

        return toResponseDTO(itemCompartilhadoRepository.save(item));
    }

    @Transactional
    public void remover(Long mesaId, Long itemId, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa nao encontrada."));
        validarAcessoMesa(mesa, usuarioLogado);

        ItemCompartilhado item = itemCompartilhadoRepository.findById(itemId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Item compartilhado nao encontrado."));

        if (!item.getMesa().getId().equals(mesaId)) {
            throw new EntidadeNaoEncontradaException("Item compartilhado nao pertence a essa mesa.");
        }

        itemCompartilhadoRepository.delete(item);
    }

    private BigDecimal obterPreco(Produto produto, TamanhoPorcao tamanho) {
        if (tamanho == TamanhoPorcao.MEIA) {
            if (produto.getPrecoMeia() == null) {
                throw new ValidacaoNegocioException("O produto '" + produto.getNome() + "' nao oferece meia porcao.");
            }
            return produto.getPrecoMeia();
        }
        return produto.getPreco();
    }

    private void validarAcessoMesa(Mesa mesa, Usuario usuarioLogado) {
        var restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(mesa.getRestaurante().getId())) {
            throw new EntidadeNaoEncontradaException("Mesa nao encontrada.");
        }
    }

    private ItemCompartilhadoResponseDTO toResponseDTO(ItemCompartilhado item) {
        ItemCompartilhadoResponseDTO dto = new ItemCompartilhadoResponseDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setNomeProduto(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setObservacao(item.getObservacao());
        dto.setTamanho(item.getTamanho());
        return dto;
    }
}
