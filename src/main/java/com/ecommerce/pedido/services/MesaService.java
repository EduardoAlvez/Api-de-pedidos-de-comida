package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.ItemCompartilhadoResponseDTO;
import com.ecommerce.pedido.dtos.MesaRequestDTO;
import com.ecommerce.pedido.dtos.MesaResponseDTO;
import com.ecommerce.pedido.models.Mesa;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.Comanda;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.models.enums.StatusComanda;
import com.ecommerce.pedido.repositories.ComandaRepository;
import com.ecommerce.pedido.repositories.MesaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.AcessoRestauranteException;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final RestauranteRepository restauranteRepository;
    private final ComandaRepository comandaRepository;

    public MesaService(MesaRepository mesaRepository, RestauranteRepository restauranteRepository,
                       ComandaRepository comandaRepository) {
        this.mesaRepository = mesaRepository;
        this.restauranteRepository = restauranteRepository;
        this.comandaRepository = comandaRepository;
    }

    @Transactional
    public MesaResponseDTO criar(MesaRequestDTO requestDTO, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(requestDTO.getRestauranteId())) {
            throw new AcessoRestauranteException("Acesso negado: voce nao pode criar mesas em outro restaurante.");
        }

        if (requestDTO.getNumero() != null) {
            if (mesaRepository.existsByRestaurante_IdAndNumero(
                    restauranteVinculado.getId(), requestDTO.getNumero())) {
                throw new ValidacaoNegocioException(
                        "Número de mesa já em uso neste restaurante: " + requestDTO.getNumero());
            }
        }

        Mesa mesa = new Mesa();
        mesa.setNomeCliente(requestDTO.getNomeCliente());
        mesa.setNumero(requestDTO.getNumero());
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setDataAbertura(LocalDateTime.now());
        mesa.setRestaurante(restauranteVinculado);
        mesa.setItensCompartilhados(Collections.emptyList());

        return toResponseDTO(mesaRepository.save(mesa));
    }

    @Transactional(readOnly = true)
    public List<MesaResponseDTO> listarPorRestaurante(Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        return mesaRepository.findAllByRestaurante_Id(restauranteVinculado.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MesaResponseDTO buscarPorId(Long id, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        validarAcessoRestaurante(mesa.getRestaurante().getId(), usuarioLogado);
        return toResponseDTO(mesa);
    }

    @Transactional
    public MesaResponseDTO atualizar(Long id, MesaRequestDTO requestDTO, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        validarAcessoRestaurante(mesa.getRestaurante().getId(), usuarioLogado);
        mesa.setNomeCliente(requestDTO.getNomeCliente());
        if (requestDTO.getNumero() != null) {
            Mesa mesaComNumero = mesaRepository.findByRestaurante_IdAndNumero(
                    mesa.getRestaurante().getId(), requestDTO.getNumero()).orElse(null);
            if (mesaComNumero != null && !mesaComNumero.getId().equals(id)) {
                throw new ValidacaoNegocioException(
                        "Número de mesa já em uso neste restaurante: " + requestDTO.getNumero());
            }
            mesa.setNumero(requestDTO.getNumero());
        }
        return toResponseDTO(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaResponseDTO encerrar(Long id, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        validarAcessoRestaurante(mesa.getRestaurante().getId(), usuarioLogado);

        List<Comanda> comandas = comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(id);
        for (Comanda c : comandas) {
            if (c.getStatus() == StatusComanda.ABERTA || c.getStatus() == StatusComanda.AGUARDANDO_PIX) {
                c.setStatus(StatusComanda.CANCELADA);
                comandaRepository.save(c);
            }
        }

        mesa.setStatus(StatusMesa.LIVRE);
        return toResponseDTO(mesaRepository.save(mesa));
    }

    @Transactional
    public void deletar(Long id, Usuario usuarioLogado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Mesa não encontrada com o ID: " + id));
        validarAcessoRestaurante(mesa.getRestaurante().getId(), usuarioLogado);

        long comandasAbertas = comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(id)
                .stream()
                .filter(c -> c.getStatus() == StatusComanda.ABERTA
                        || c.getStatus() == StatusComanda.AGUARDANDO_PIX)
                .count();
        if (comandasAbertas > 0) {
            throw new ValidacaoNegocioException("Não é possível remover a mesa: existem comandas em aberto.");
        }

        mesaRepository.deleteById(id);
    }

    private void validarAcessoRestaurante(Long restauranteId, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(restauranteId)) {
            throw new EntidadeNaoEncontradaException("Mesa não encontrada com o ID: " + restauranteId);
        }
    }

    private MesaResponseDTO toResponseDTO(Mesa mesa) {
        MesaResponseDTO response = new MesaResponseDTO();
        response.setId(mesa.getId());
        response.setNomeCliente(mesa.getNomeCliente());
        response.setNumero(mesa.getNumero());
        response.setStatus(mesa.getStatus());
        response.setDataAbertura(mesa.getDataAbertura());
        if (mesa.getRestaurante() != null) {
            response.setRestauranteId(mesa.getRestaurante().getId());
            response.setRestauranteNome(mesa.getRestaurante().getNome());
        }
        response.setItensCompartilhados(
            Optional.ofNullable(mesa.getItensCompartilhados())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    ItemCompartilhadoResponseDTO dto = new ItemCompartilhadoResponseDTO();
                    dto.setId(item.getId());
                    dto.setProdutoId(item.getProduto().getId());
                    dto.setNomeProduto(item.getProduto().getNome());
                    dto.setQuantidade(item.getQuantidade());
                    dto.setPrecoUnitario(item.getPrecoUnitario());
                    dto.setObservacao(item.getObservacao());
                    return dto;
                })
                .collect(Collectors.toList())
        );
        return response;
    }
}
