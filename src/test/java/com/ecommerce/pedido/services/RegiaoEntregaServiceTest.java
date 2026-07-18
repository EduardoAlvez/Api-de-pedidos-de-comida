package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.RegiaoEntregaRequestDTO;
import com.ecommerce.pedido.dtos.RegiaoEntregaResponseDTO;
import com.ecommerce.pedido.models.RegiaoEntrega;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.repositories.RegiaoEntregaRepository;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.RegiaoEntregaNaoEncontradaException;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Feature("Regiões de Entrega")
@Story("Service")
class RegiaoEntregaServiceTest extends BaseServiceTest {

    @Mock
    private RegiaoEntregaRepository regiaoEntregaRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private RegiaoEntregaService regiaoEntregaService;

    private Restaurante restaurante;
    private Usuario usuarioLogado;
    private RegiaoEntrega regiaoCentro;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");

        usuarioLogado = new Usuario();
        usuarioLogado.setId(2L);
        usuarioLogado.setTipo(Role.GARCOM);
        usuarioLogado.setRestauranteTrabalho(restaurante);

        regiaoCentro = new RegiaoEntrega();
        regiaoCentro.setId(1L);
        regiaoCentro.setNome("Centro");
        regiaoCentro.setValorFrete(new BigDecimal("8.00"));
        regiaoCentro.setRestaurante(restaurante);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Consulta")
    void deveListarRegioesPorRestaurante() {
        when(regiaoEntregaRepository.findAllByRestaurante_Id(1L))
                .thenReturn(List.of(regiaoCentro));

        List<RegiaoEntregaResponseDTO> response = regiaoEntregaService.listar(usuarioLogado);

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("Centro", response.getFirst().getNome());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Consulta")
    void deveBuscarRegiaoPorIdExistente() {
        when(regiaoEntregaRepository.findById(1L)).thenReturn(Optional.of(regiaoCentro));

        RegiaoEntregaResponseDTO response = regiaoEntregaService.buscarPorId(1L, usuarioLogado);

        assertNotNull(response);
        assertEquals("Centro", response.getNome());
        assertEquals(0, new BigDecimal("8.00").compareTo(response.getValorFrete()));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Validação")
    void deveLancarExcecao_quandoRegiaoNaoExiste() {
        when(regiaoEntregaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RegiaoEntregaNaoEncontradaException.class,
                () -> regiaoEntregaService.buscarPorId(99L, usuarioLogado));
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarRegiaoComDadosValidos() {
        RegiaoEntregaRequestDTO request = new RegiaoEntregaRequestDTO();
        request.setNome("Centro");
        request.setValorFrete(new BigDecimal("8.00"));

        when(regiaoEntregaRepository.save(any(RegiaoEntrega.class))).thenReturn(regiaoCentro);

        RegiaoEntregaResponseDTO response = regiaoEntregaService.criar(request, usuarioLogado);

        assertNotNull(response);
        assertEquals("Centro", response.getNome());
    }


}
