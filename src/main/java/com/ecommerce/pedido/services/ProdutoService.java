package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.ProdutoRequestDTO;
import com.ecommerce.pedido.dtos.ProdutoResponseDTO;
import com.ecommerce.pedido.models.Produto;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.repositories.ProdutoRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.ProdutoNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;

    public ProdutoService(ProdutoRepository produtoRepository, RestauranteRepository restauranteRepository) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO requestDTO) {
        // O restaurante informado para o produto deve existir.
        Restaurante restaurante = restauranteRepository.findById(requestDTO.getRestauranteId())
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante não encontrado com o ID: " + requestDTO.getRestauranteId()));

        // Cria a entidade Produto e copia os dados do DTO
        Produto produto = new Produto();
        BeanUtils.copyProperties(requestDTO, produto, "restauranteId");

        produto.setRestaurante(restaurante);
        return toResponseDTO(produtoRepository.save(produto));
    }


    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado com o ID: " + id));
        return toResponseDTO(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId) {
        // Garante que o restaurante existe antes de listar seus produtos.
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new RestauranteNaoEncontradoException("Restaurante não encontrado com o ID: " + restauranteId);
        }

        List<Produto> produtos = produtoRepository.findAllByRestaurante_Id(restauranteId);
        return produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO requestDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado com o ID: " + id));

        // Não vamos permitir a alteração do restaurante de um produto por este metodo.
        BeanUtils.copyProperties(requestDTO, produto, "restauranteId");
        return toResponseDTO(produtoRepository.save(produto));
    }


    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado com o ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        ProdutoResponseDTO response = new ProdutoResponseDTO();
        BeanUtils.copyProperties(produto, response);

        ProdutoResponseDTO.RestauranteSimpleDTO restDTO = new ProdutoResponseDTO.RestauranteSimpleDTO();
        BeanUtils.copyProperties(produto.getRestaurante(), restDTO);

        response.setRestaurante(restDTO);
        return response;
    }
}