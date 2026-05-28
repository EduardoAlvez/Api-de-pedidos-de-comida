package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.MesaRequestDTO;
import com.ecommerce.pedido.dtos.MesaResponseDTO;
import com.ecommerce.pedido.models.Mesa;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.repositories.MesaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final RestauranteRepository restauranteRepository;

    public MesaService(MesaRepository mesaRepository, RestauranteRepository restauranteRepository) {
        this.mesaRepository = mesaRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional
    public MesaResponseDTO criar(MesaRequestDTO requestDTO) {
        Restaurante restaurante = restauranteRepository.findById(requestDTO.getRestauranteId())
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante não encontrado."));

        Mesa mesa = new Mesa();
        mesa.setNomeCliente(requestDTO.getNomeCliente());
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setDataAbertura(LocalDateTime.now());
        mesa.setRestaurante(restaurante);

        return toResponseDTO(mesaRepository.save(mesa));
    }

    @Transactional(readOnly = true)
    public List<MesaResponseDTO> listarPorRestaurante(Long restauranteId) {
        return mesaRepository.findAllByRestaurante_Id(restauranteId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MesaResponseDTO buscarPorId(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        return toResponseDTO(mesa);
    }

    @Transactional
    public MesaResponseDTO atualizar(Long id, MesaRequestDTO requestDTO) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        mesa.setNomeCliente(requestDTO.getNomeCliente());
        return toResponseDTO(mesaRepository.save(mesa));
    }

    @Transactional
    public void deletar(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id);
        }
        mesaRepository.deleteById(id);
    }

    private MesaResponseDTO toResponseDTO(Mesa mesa) {
        MesaResponseDTO response = new MesaResponseDTO();
        response.setId(mesa.getId());
        response.setNomeCliente(mesa.getNomeCliente());
        response.setStatus(mesa.getStatus());
        response.setDataAbertura(mesa.getDataAbertura());
        if (mesa.getRestaurante() != null) {
            response.setRestauranteId(mesa.getRestaurante().getId());
            response.setRestauranteNome(mesa.getRestaurante().getNome());
        }
        return response;
    }
}
