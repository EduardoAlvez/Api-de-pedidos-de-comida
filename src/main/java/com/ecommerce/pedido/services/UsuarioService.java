package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.UsuarioRequestDTO;
import com.ecommerce.pedido.dtos.UsuarioResponseDTO;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.repositories.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO requestDTO) {
        // Verificar se o e-mail já está em uso
        if (usuarioRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        BeanUtils.copyProperties(requestDTO, novoUsuario, "id", "senha");

        novoUsuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return toResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));
        return toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));

        boolean emailAlterado = !usuario.getEmail().equals(requestDTO.getEmail());
        boolean emailEmUso = usuarioRepository.findByEmail(requestDTO.getEmail()).isPresent();

        if (emailAlterado && emailEmUso) {
            throw new RuntimeException("O novo e-mail já está em uso por outro usuário.");
        }

        BeanUtils.copyProperties(requestDTO, usuario, "id", "senha");

        if (requestDTO.getSenha() != null && !requestDTO.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        }

        return toResponseDTO(usuarioRepository.save(usuario));
    }


    @Transactional
    public void deletar(Long id) {
        // 1. Verifica se o usuário existe antes de tentar deletar.
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // Converte uma Entidade Usuario para um UsuarioResponseDTO
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        BeanUtils.copyProperties(usuario, responseDTO);
        return responseDTO;
    }

}