package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.RegiaoEntregaRequestDTO;
import com.ecommerce.pedido.dtos.RegiaoEntregaResponseDTO;
import com.ecommerce.pedido.models.RegiaoEntrega;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.repositories.RegiaoEntregaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.RegiaoEntregaNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
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
    public List<RegiaoEntregaResponseDTO> listar(Long restauranteId) {
        return regiaoEntregaRepository.findAllByRestaurante_Id(restauranteId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegiaoEntregaResponseDTO buscarPorId(Long id) {
        RegiaoEntrega regiao = regiaoEntregaRepository.findById(id)
                .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id));
        return toResponseDTO(regiao);
    }

    @Transactional
    public RegiaoEntregaResponseDTO criar(Long restauranteId, RegiaoEntregaRequestDTO requestDTO) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante não encontrado com o ID: " + restauranteId));

        RegiaoEntrega regiao = new RegiaoEntrega();
        BeanUtils.copyProperties(requestDTO, regiao);
        regiao.setRestaurante(restaurante);

        return toResponseDTO(regiaoEntregaRepository.save(regiao));
    }

    @Transactional
    public RegiaoEntregaResponseDTO atualizar(Long id, RegiaoEntregaRequestDTO requestDTO) {
        RegiaoEntrega regiao = regiaoEntregaRepository.findById(id)
                .orElseThrow(() -> new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id));

        BeanUtils.copyProperties(requestDTO, regiao);
        return toResponseDTO(regiaoEntregaRepository.save(regiao));
    }

    @Transactional
    public void deletar(Long id) {
        if (!regiaoEntregaRepository.existsById(id)) {
            throw new RegiaoEntregaNaoEncontradaException("Região de entrega não encontrada com o ID: " + id);
        }
        regiaoEntregaRepository.deleteById(id);
    }

    private RegiaoEntregaResponseDTO toResponseDTO(RegiaoEntrega regiao) {
        RegiaoEntregaResponseDTO response = new RegiaoEntregaResponseDTO();
        BeanUtils.copyProperties(regiao, response);
        return response;
    }
}
