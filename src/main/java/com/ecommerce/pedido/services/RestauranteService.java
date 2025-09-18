package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dto.RestauranteDTO;
import com.ecommerce.pedido.models.Cliente;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.repositories.ClienteRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final ClienteRepository clienteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository, ClienteRepository clienteRepository) {
        this.restauranteRepository = restauranteRepository;
        this.clienteRepository = clienteRepository;
    }

    public Restaurante criarRestaurante(RestauranteDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // regra: só pode criar restaurante se for role RESTAURANTE
        if (cliente.getTipo() != Role.RESTAURANTE) {
            throw new IllegalArgumentException("Apenas clientes com role RESTAURANTE podem cadastrar restaurantes");
        }

        // regra: cliente só pode ter 1 restaurante
        if (cliente.getRestaurante() != null) {
            throw new IllegalArgumentException("Cliente já possui um restaurante cadastrado");
        }

        Restaurante restaurante = new Restaurante();
        BeanUtils.copyProperties(dto, restaurante);
        restaurante.setCliente(cliente);

        return restauranteRepository.save(restaurante);
    }
}

