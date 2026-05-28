package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.ItemPedidoRequestDTO;
import com.ecommerce.pedido.dtos.PedidoRequestDTO;
import com.ecommerce.pedido.dtos.PedidoResponseDTO;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusPedido;
import com.ecommerce.pedido.repositories.*;
import com.ecommerce.pedido.services.exceptions.RegiaoEntregaNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Feature("Pedidos")
@Story("Service")
class PedidoServiceTest extends BaseServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private RegiaoEntregaRepository regiaoEntregaRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Restaurante restaurante;
    private Usuario cliente;
    private Produto feijoada;
    private RegiaoEntrega regiaoCentro;
    private PedidoRequestDTO requestValido;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");

        cliente = new Usuario();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");

        feijoada = new Produto();
        feijoada.setId(1L);
        feijoada.setNome("Feijoada");
        feijoada.setPreco(new BigDecimal("35.90"));
        feijoada.setRestaurante(restaurante);

        regiaoCentro = new RegiaoEntrega();
        regiaoCentro.setId(1L);
        regiaoCentro.setNome("Centro");
        regiaoCentro.setValorFrete(new BigDecimal("8.00"));
        regiaoCentro.setRestaurante(restaurante);

        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(1L);
        item.setQuantidade(1);

        requestValido = new PedidoRequestDTO();
        requestValido.setRestauranteId(1L);
        requestValido.setUsuarioId(1L);
        requestValido.setItens(List.of(item));
        requestValido.setEnderecoDeEntrega("Rua Teste, 123");
        requestValido.setRegiaoEntregaId(1L);
        requestValido.setFormaDePagamento(FormaPagamento.PIX);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarPedidoDeliveryComDadosValidos() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(feijoada));
        when(regiaoEntregaRepository.findById(1L)).thenReturn(Optional.of(regiaoCentro));

        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setId(1L);
        pedidoSalvo.setRestaurante(restaurante);
        pedidoSalvo.setUsuario(cliente);
        pedidoSalvo.setStatus(StatusPedido.AGUARDANDO_CONFIRMACAO);
        pedidoSalvo.setSubtotal(new BigDecimal("35.90"));
        pedidoSalvo.setTaxaEntrega(new BigDecimal("8.00"));
        pedidoSalvo.setValorTotal(new BigDecimal("43.90"));
        pedidoSalvo.setEnderecoDeEntrega("Rua Teste, 123");
        pedidoSalvo.setItens(new ArrayList<>());

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        PedidoResponseDTO response = pedidoService.criar(requestValido);

        assertNotNull(response);
        assertEquals(StatusPedido.AGUARDANDO_CONFIRMACAO, response.getStatus());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Validação")
    void deveLancarExcecao_quandoRegiaoNaoPertenceAoRestaurante() {
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(2L);
        outroRestaurante.setNome("Outro Restaurante");

        RegiaoEntrega regiaoDeOutro = new RegiaoEntrega();
        regiaoDeOutro.setId(1L);
        regiaoDeOutro.setNome("Centro");
        regiaoDeOutro.setValorFrete(new BigDecimal("8.00"));
        regiaoDeOutro.setRestaurante(outroRestaurante);

        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(feijoada));
        when(regiaoEntregaRepository.findById(1L)).thenReturn(Optional.of(regiaoDeOutro));

        assertThrows(ValidacaoNegocioException.class,
                () -> pedidoService.criar(requestValido));
    }
}
