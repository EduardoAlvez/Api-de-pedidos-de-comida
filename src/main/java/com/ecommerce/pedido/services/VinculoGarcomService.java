package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.GarcomRequestDTO;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.repositories.UsuarioRepository;
import com.ecommerce.pedido.services.exceptions.AcessoRestauranteException;
import com.ecommerce.pedido.services.exceptions.EmailCadastradoExcption;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.UsuarioNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VinculoGarcomService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VinculoGarcomService(RestauranteRepository restauranteRepository,
                                 UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO criarGarcom(GarcomRequestDTO dto, Long restauranteId, Usuario donoLogado) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante nao encontrado."));

        if (!restaurante.getUsuario().getId().equals(donoLogado.getId())) {
            throw new AcessoRestauranteException("Voce nao e dono deste restaurante.");
        }

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailCadastradoExcption("Este e-mail ja esta cadastrado.");
        }

        Usuario novoGarcom = new Usuario();
        BeanUtils.copyProperties(dto, novoGarcom, "id", "senha");

        novoGarcom.setTipo(Role.GARCOM);
        novoGarcom.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoGarcom.setRestauranteTrabalho(restaurante);

        Usuario garcomSalvo = usuarioRepository.save(novoGarcom);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        BeanUtils.copyProperties(garcomSalvo, response);
        return response;
    }

    @Transactional
    public void vincular(Long restauranteId, Long usuarioId, Usuario donoLogado) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante nao encontrado."));

        if (!restaurante.getUsuario().getId().equals(donoLogado.getId())) {
            throw new AcessoRestauranteException("Voce nao e dono deste restaurante.");
        }

        Usuario garcom = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado."));

        if (garcom.getTipo() != Role.GARCOM) {
            throw new ValidacaoNegocioException("Usuario deve ter role GARCOM.");
        }

        if (garcom.getRestauranteTrabalho() != null) {
            throw new ValidacaoNegocioException("Garcom ja vinculado a outro restaurante.");
        }

        garcom.setRestauranteTrabalho(restaurante);
        usuarioRepository.save(garcom);
    }

    @Transactional
    public void desvincular(Long restauranteId, Long usuarioId, Usuario donoLogado) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RestauranteNaoEncontradoException("Restaurante nao encontrado."));

        if (!restaurante.getUsuario().getId().equals(donoLogado.getId())) {
            throw new AcessoRestauranteException("Voce nao e dono deste restaurante.");
        }

        Usuario garcom = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado."));

        if (garcom.getRestauranteTrabalho() == null
                || !garcom.getRestauranteTrabalho().getId().equals(restauranteId)) {
            throw new ValidacaoNegocioException("Garcom nao esta vinculado a este restaurante.");
        }

        garcom.setRestauranteTrabalho(null);
        usuarioRepository.save(garcom);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarGarcons(Long restauranteId) {
        return usuarioRepository.findAllByRestauranteTrabalhoId(restauranteId);
    }
}
