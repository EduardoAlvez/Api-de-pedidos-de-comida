package com.ecommerce.pedido.services;

import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import com.ecommerce.pedido.repositories.UsuarioRepository;
import com.ecommerce.pedido.services.exceptions.AcessoRestauranteException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.UsuarioNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Feature("Vincular Garçom")
@Story("Service")
class VinculoGarcomServiceTest extends BaseServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private VinculoGarcomService vinculoGarcomService;

    private Restaurante restaurante;
    private Usuario dono;
    private Usuario garcom;
    private Usuario garcomJaVinculado;
    private Usuario cliente;

    @BeforeEach
    void setUp() {
        // Dono do restaurante
        dono = new Usuario();
        dono.setId(1L);
        dono.setTipo(Role.DONO_RESTAURANTE);

        // Garcom disponivel (sem vinculo)
        garcom = new Usuario();
        garcom.setId(2L);
        garcom.setTipo(Role.GARCOM);
        garcom.setRestauranteTrabalho(null);

        // Garcom ja vinculado a outro restaurante
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(99L);
        garcomJaVinculado = new Usuario();
        garcomJaVinculado.setId(3L);
        garcomJaVinculado.setTipo(Role.GARCOM);
        garcomJaVinculado.setRestauranteTrabalho(outroRestaurante);

        // Cliente (role errada)
        cliente = new Usuario();
        cliente.setId(4L);
        cliente.setTipo(Role.CLIENTE);

        // Restaurante pertencente ao dono
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setUsuario(dono);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    void deveVincularGarcom_comDonoValido() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(garcom));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(garcom);

        vinculoGarcomService.vincular(1L, 2L, dono);

        verify(usuarioRepository).save(garcom);
        assertNotNull(garcom.getRestauranteTrabalho());
        assertEquals(1L, garcom.getRestauranteTrabalho().getId());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveLancarExcecao_quandoDonoNaoPertenceAoRestaurante() {
        Usuario outroDono = new Usuario();
        outroDono.setId(99L);
        outroDono.setTipo(Role.DONO_RESTAURANTE);

        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThrows(AcessoRestauranteException.class,
                () -> vinculoGarcomService.vincular(1L, 2L, outroDono));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveLancarExcecao_quandoUsuarioNaoEGarcom() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(cliente));

        assertThrows(ValidacaoNegocioException.class,
                () -> vinculoGarcomService.vincular(1L, 4L, dono));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveLancarExcecao_quandoGarcomJaVinculado() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(garcomJaVinculado));

        assertThrows(ValidacaoNegocioException.class,
                () -> vinculoGarcomService.vincular(1L, 3L, dono));
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    void deveDesvincularGarcom_comDonoValido() {
        // Configura garcom vinculado ao restaurante do dono
        garcom.setRestauranteTrabalho(restaurante);

        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(garcom));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(garcom);

        vinculoGarcomService.desvincular(1L, 2L, dono);

        verify(usuarioRepository).save(garcom);
        assertNull(garcom.getRestauranteTrabalho());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveLancarExcecao_quandoGarcomNaoVinculadoAoRestaurante() {
        // garcom nao tem restauranteTrabalho
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(garcom));

        assertThrows(ValidacaoNegocioException.class,
                () -> vinculoGarcomService.desvincular(1L, 2L, dono));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveListarGarcons_doRestaurante() {
        when(usuarioRepository.findAllByRestauranteTrabalhoId(1L))
                .thenReturn(List.of(garcom));

        List<Usuario> garcons = vinculoGarcomService.listarGarcons(1L);

        assertFalse(garcons.isEmpty());
        assertEquals(1, garcons.size());
        assertEquals(2L, garcons.get(0).getId());
    }
}
