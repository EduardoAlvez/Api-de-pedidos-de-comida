package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dto.ClienteDTO;
import com.ecommerce.pedido.models.Cliente;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.repositories.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        BeanUtils.copyProperties(dto, cliente);

        // Senha obrigatória se for RESTAURANTE
        if (cliente.getTipo() == Role.RESTAURANTE && (cliente.getSenha() == null || cliente.getSenha().isBlank())) {
            throw new IllegalArgumentException("Restaurante deve ter senha para login");
        }

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
}

