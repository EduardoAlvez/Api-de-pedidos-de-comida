package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.RegiaoEntregaRequestDTO;
import com.ecommerce.pedido.dtos.RegiaoEntregaResponseDTO;
import com.ecommerce.pedido.models.RegiaoEntrega;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.repositories.RegiaoEntregaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.RegiaoEntregaNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegiaoEntregaService {

    private final RegiaoEntregaRepository regiaoEntregaRepository;
    private final RestauranteRepository restauranteRepository;

    public RegiaoEntregaService(RegiaoEntregaRepository regiaoEntregaRepository, RestauranteRepository restauranteRepository) {
        this.regiaoEntregaRepository = regiaoEntregaRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional(readOnly = true)
    public List<RegiaoEntregaResponseDTO> listar(Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        return regiaoEntregaRepository.findAllByRestaurante_Id(restauranteVinculado.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegiaoEntregaResponseDTO buscarPorId(Long id, Usuario usuarioLogado) {
        RegiaoEntrega regiao = regiaoEntregaRepository.findById(id)
                .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id));
        validarAcessoRestaurante(regiao.getRestaurante().getId(), usuarioLogado);
        return toResponseDTO(regiao);
    }

    @Transactional
    public RegiaoEntregaResponseDTO criar(RegiaoEntregaRequestDTO requestDTO, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }

        RegiaoEntrega regiao = new RegiaoEntrega();
        BeanUtils.copyProperties(requestDTO, regiao);
        regiao.setRestaurante(restauranteVinculado);

        return toResponseDTO(regiaoEntregaRepository.save(regiao));
    }

    @Transactional
    public RegiaoEntregaResponseDTO atualizar(Long id, RegiaoEntregaRequestDTO requestDTO, Usuario usuarioLogado) {
        RegiaoEntrega regiao = regiaoEntregaRepository.findById(id)
                .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id));
        validarAcessoRestaurante(regiao.getRestaurante().getId(), usuarioLogado);

        BeanUtils.copyProperties(requestDTO, regiao);
        return toResponseDTO(regiaoEntregaRepository.save(regiao));
    }

    @Transactional
    public void deletar(Long id, Usuario usuarioLogado) {
        RegiaoEntrega regiao = regiaoEntregaRepository.findById(id)
                .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id));
        validarAcessoRestaurante(regiao.getRestaurante().getId(), usuarioLogado);
        regiaoEntregaRepository.deleteById(id);
    }

    private void validarAcessoRestaurante(Long restauranteId, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(restauranteId)) {
            throw new EntidadeNaoEncontradaException("Região de entrega não encontrada com o ID: " + restauranteId);
        }
    }

    private RegiaoEntregaResponseDTO toResponseDTO(RegiaoEntrega regiao) {
        RegiaoEntregaResponseDTO response = new RegiaoEntregaResponseDTO();
        BeanUtils.copyProperties(regiao, response);
        return response;
    }
}
