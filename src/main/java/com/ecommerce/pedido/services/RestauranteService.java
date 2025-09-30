package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.RestauranteRequestDTO;
import com.ecommerce.pedido.dtos.RestauranteResponseDTO;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.repositories.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public RestauranteResponseDTO criar(RestauranteRequestDTO requestDTO) {
        // Buscar o usurio que será o dono. Se não existir, lança exceção.
        Usuario dono = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário dono não encontrado com o ID: " + requestDTO.getUsuarioId()));

        // Verificar se este usuário já é dono de outro restaurante.
        if (restauranteRepository.existsByUsuario_Id(dono.getId())) {
            throw new RuntimeException("Este usuário já é dono de um restaurante.");
        }

        // Verificar se o CNPJ já está em uso.
        restauranteRepository.findByCnpj(requestDTO.getCnpj()).ifPresent(r -> {
            throw new RuntimeException("Este CNPJ já está cadastrado.");
        });

        // Cria e associa o restaurante ao dono.
        Restaurante restaurante = new Restaurante();
        BeanUtils.copyProperties(requestDTO, restaurante, "usuarioId"); // Evita copiar o ID do usuário diretamente
        restaurante.setUsuario(dono);

        // Salva e retorna o DTO de resposta.
        return toResponseDTO(restauranteRepository.save(restaurante));
    }


    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> listarTodos() {
        return restauranteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com o ID: " + id));
        return toResponseDTO(restaurante);
    }

    @Transactional
    public RestauranteResponseDTO atualizar(Long id, RestauranteRequestDTO requestDTO) {
        // Busca o restaurante existente no banco de dados.
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com o ID: " + id));

        // Validação de CNPJ único na atualização.
        restauranteRepository.findByCnpj(requestDTO.getCnpj()).ifPresent(r -> {
            if (!r.getId().equals(id)) {
                throw new RuntimeException("Este CNPJ já está em uso por outro restaurante.");
            }
        });

        BeanUtils.copyProperties(requestDTO, restaurante, "usuarioId");
        return toResponseDTO(restauranteRepository.save(restaurante));
    }


    @Transactional
    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new RuntimeException("Restaurante não encontrado com o ID: " + id);
        }
        restauranteRepository.deleteById(id);
    }

    private RestauranteResponseDTO toResponseDTO(Restaurante restaurante) {
        RestauranteResponseDTO response = new RestauranteResponseDTO();
        BeanUtils.copyProperties(restaurante, response, "usuario");

        RestauranteResponseDTO.DonoRestauranteDTO donoDTO = new RestauranteResponseDTO.DonoRestauranteDTO();
        BeanUtils.copyProperties(restaurante.getUsuario(), donoDTO, "senha", "email", "telefone");
        response.setDono(donoDTO);

        return response;
    }

}