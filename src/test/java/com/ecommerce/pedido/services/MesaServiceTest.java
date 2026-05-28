package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.MesaRequestDTO;
import com.ecommerce.pedido.dtos.MesaResponseDTO;
import com.ecommerce.pedido.models.Mesa;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.repositories.MesaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Feature("Mesas")
@Story("Service")
class MesaServiceTest extends BaseServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private MesaService mesaService;

    private Restaurante restaurante;
    private MesaRequestDTO requestValido;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");

        requestValido = new MesaRequestDTO();
        requestValido.setNomeCliente("Cliente Teste");
        requestValido.setRestauranteId(1L);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarMesaComDadosValidos() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        Mesa mesaSalva = new Mesa();
        mesaSalva.setId(1L);
        mesaSalva.setNomeCliente("Cliente Teste");
        mesaSalva.setStatus(StatusMesa.LIVRE);
        mesaSalva.setRestaurante(restaurante);

        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaSalva);

        MesaResponseDTO response = mesaService.criar(requestValido);

        assertNotNull(response);
        assertEquals("Cliente Teste", response.getNomeCliente());
        assertEquals(StatusMesa.LIVRE, response.getStatus());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Consulta")
    void deveBuscarMesaPorIdExistente() {
        Mesa mesa = new Mesa();
        mesa.setId(1L);
        mesa.setNomeCliente("Cliente Teste");
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setRestaurante(restaurante);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        MesaResponseDTO response = mesaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Cliente Teste", response.getNomeCliente());
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Validação")
    void deveLancarExcecao_quandoBuscarMesaPorIdInexistente() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RestauranteNaoEncontradoException.class,
                () -> mesaService.buscarPorId(99L));
    }
}
