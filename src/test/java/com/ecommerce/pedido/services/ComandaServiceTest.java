package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.ComandaItemRequestDTO;
import com.ecommerce.pedido.dtos.ComandaRequestDTO;
import com.ecommerce.pedido.dtos.ComandaResponseDTO;
import com.ecommerce.pedido.dtos.RateioRequestDTO;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.FormaPagamento;
import com.ecommerce.pedido.models.enums.StatusComanda;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.repositories.*;
import com.ecommerce.pedido.services.exceptions.ProdutoNaoEncontradoException;
import com.ecommerce.pedido.services.exceptions.RestauranteNaoEncontradoException;
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

@Feature("Comandas")
@Story("Service")
class ComandaServiceTest extends BaseServiceTest {

    @Mock
    private ComandaRepository comandaRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ComandaRateioRepository comandaRateioRepository;

    @InjectMocks
    private ComandaService comandaService;

    private Mesa mesaLivre;
    private Mesa mesaOcupada;
    private Usuario garcom;
    private Produto feijoada;
    private Produto refri;
    private ComandaRequestDTO requestDTO;
    private ComandaRequestDTO requestDTOCompartilhado;

    @BeforeEach
    void setUp() {
        mesaLivre = new Mesa();
        mesaLivre.setId(1L);
        mesaLivre.setNomeCliente("Teste");
        mesaLivre.setStatus(StatusMesa.LIVRE);

        mesaOcupada = new Mesa();
        mesaOcupada.setId(2L);
        mesaOcupada.setNomeCliente("Teste");
        mesaOcupada.setStatus(StatusMesa.OCUPADA);

        garcom = new Usuario();
        garcom.setId(1L);
        garcom.setNome("Garcom Teste");
        garcom.setEmail("garcom@teste.com");

        feijoada = new Produto();
        feijoada.setId(1L);
        feijoada.setNome("Feijoada");
        feijoada.setPreco(new BigDecimal("35.90"));

        refri = new Produto();
        refri.setId(2L);
        refri.setNome("Refrigerante");
        refri.setPreco(new BigDecimal("12.00"));

        ComandaItemRequestDTO item1 = new ComandaItemRequestDTO();
        item1.setProdutoId(1L);
        item1.setQuantidade(1);
        item1.setCompartilhado(false);

        requestDTO = new ComandaRequestDTO();
        requestDTO.setClienteNome("Joao");
        requestDTO.setItens(List.of(item1));
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Criação")
    void deveCriarComandaEmMesaLivre_eMarcarMesaComoOcupada() {
        Allure.step("Configurar mocks", () -> {
            when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaLivre));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(garcom));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(feijoada));
        });

        Allure.step("Criar comanda salva mock", () -> {
            Comanda comandaSalva = new Comanda();
            comandaSalva.setId(1L);
            comandaSalva.setMesa(mesaLivre);
            comandaSalva.setGarcom(garcom);
            comandaSalva.setClienteNome("Joao");
            comandaSalva.setStatus(StatusComanda.ABERTA);
            comandaSalva.setValorTotal(new BigDecimal("35.90"));
            comandaSalva.setItens(new ArrayList<>());
            comandaSalva.setRateios(new ArrayList<>());

            when(comandaRepository.save(any(Comanda.class))).thenReturn(comandaSalva);
        });

        ComandaResponseDTO response = Allure.step("Executar service.criar()", () ->
            comandaService.criar(1L, 1L, requestDTO)
        );

        Allure.step("Verificar resultado", () -> {
            assertNotNull(response);
            assertEquals(StatusComanda.ABERTA, response.getStatus());
            assertEquals("Joao", response.getClienteNome());
            verify(mesaRepository).save(mesaLivre);
            assertEquals(StatusMesa.OCUPADA, mesaLivre.getStatus());
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Criação")
    void deveCriarComandaEmMesaOcupada_eManterOcupada() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaOcupada));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(garcom));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(feijoada));

        Comanda comandaSalva = new Comanda();
        comandaSalva.setId(1L);
        comandaSalva.setMesa(mesaOcupada);
        comandaSalva.setGarcom(garcom);
        comandaSalva.setClienteNome("Joao");
        comandaSalva.setStatus(StatusComanda.ABERTA);
        comandaSalva.setValorTotal(new BigDecimal("35.90"));
        comandaSalva.setItens(new ArrayList<>());
        comandaSalva.setRateios(new ArrayList<>());

        when(comandaRepository.save(any(Comanda.class))).thenReturn(comandaSalva);

        ComandaResponseDTO response = comandaService.criar(1L, 1L, requestDTO);

        assertNotNull(response);
        verify(mesaRepository, never()).save(any());
        assertEquals(StatusMesa.OCUPADA, mesaOcupada.getStatus());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Validação")
    void deveLancarExcecao_quandoMesaNaoExiste() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RestauranteNaoEncontradoException.class,
                () -> comandaService.criar(99L, 1L, requestDTO));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Validação")
    void deveLancarExcecao_quandoProdutoNaoExiste() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaLivre));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(garcom));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        ComandaItemRequestDTO itemInvalido = new ComandaItemRequestDTO();
        itemInvalido.setProdutoId(99L);
        itemInvalido.setQuantidade(1);
        itemInvalido.setCompartilhado(false);

        ComandaRequestDTO dto = new ComandaRequestDTO();
        dto.setClienteNome("Joao");
        dto.setItens(List.of(itemInvalido));

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> comandaService.criar(1L, 1L, dto));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Rateio")
    void deveAceitarRateio_quandoValorNaoExcedeSaldo() {
        Comanda comanda = criarComandaBase();

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(refri));
        when(comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(1L))
                .thenReturn(List.of(comanda));

        RateioRequestDTO rateioDTO = new RateioRequestDTO();
        rateioDTO.setProdutoId(2L);
        rateioDTO.setValorPago(new BigDecimal("5.00"));

        when(comandaRateioRepository.save(any(ComandaRateio.class))).thenReturn(new ComandaRateio());
        when(comandaRepository.save(any(Comanda.class))).thenReturn(comanda);

        ComandaResponseDTO response = comandaService.rateio(1L, rateioDTO);

        assertNotNull(response);
        verify(comandaRateioRepository).save(any(ComandaRateio.class));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Rateio")
    void deveRejeitarRateio_quandoValorExcedeSaldo() {
        Comanda comanda = criarComandaBase();

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(refri));
        when(comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(1L))
                .thenReturn(List.of(comanda));

        RateioRequestDTO rateioDTO = new RateioRequestDTO();
        rateioDTO.setProdutoId(2L);
        rateioDTO.setValorPago(new BigDecimal("999.00"));

        assertThrows(ValidacaoNegocioException.class,
                () -> comandaService.rateio(1L, rateioDTO));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Fechamento")
    void deveFecharComanda_eAlterarStatusParaPaga() {
        Comanda comanda = criarComandaBase();
        comanda.setStatus(StatusComanda.ABERTA);

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(comandaRepository.countByMesa_IdAndStatus(1L, StatusComanda.ABERTA)).thenReturn(0L);
        when(comandaRepository.countByMesa_IdAndStatus(1L, StatusComanda.AGUARDANDO_PIX)).thenReturn(0L);
        when(comandaRepository.save(any(Comanda.class))).thenReturn(comanda);

        ComandaResponseDTO response = comandaService.fechar(1L, FormaPagamento.DINHEIRO);

        assertNotNull(response);
        assertEquals(StatusComanda.PAGA, comanda.getStatus());
        assertNotNull(comanda.getDataFechamento());
        verify(mesaRepository).save(any(Mesa.class));
        assertEquals(StatusMesa.LIVRE, comanda.getMesa().getStatus());
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("Fechamento")
    void deveLancarExcecao_quandoFecharComandaJaPaga() {
        Comanda comanda = criarComandaBase();
        comanda.setStatus(StatusComanda.PAGA);

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));

        assertThrows(ValidacaoNegocioException.class,
                () -> comandaService.fechar(1L, FormaPagamento.DINHEIRO));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Fechamento")
    void deveFecharComanda_eManterMesaOcupada_quandoHaOutrasAbertas() {
        Comanda comanda = criarComandaBase();
        comanda.setStatus(StatusComanda.ABERTA);

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(comandaRepository.countByMesa_IdAndStatus(1L, StatusComanda.ABERTA)).thenReturn(1L);
        when(comandaRepository.countByMesa_IdAndStatus(1L, StatusComanda.AGUARDANDO_PIX)).thenReturn(0L);
        when(comandaRepository.save(any(Comanda.class))).thenReturn(comanda);

        ComandaResponseDTO response = comandaService.fechar(1L, FormaPagamento.DINHEIRO);

        assertNotNull(response);
        assertEquals(StatusComanda.PAGA, comanda.getStatus());
        verify(mesaRepository, never()).save(any(Mesa.class));
        assertEquals(StatusMesa.OCUPADA, comanda.getMesa().getStatus());
    }

    private Comanda criarComandaBase() {
        Mesa mesa = new Mesa();
        mesa.setId(1L);
        mesa.setStatus(StatusMesa.OCUPADA);

        List<ComandaItem> itens = new ArrayList<>();
        ComandaItem item = new ComandaItem();
        item.setId(1L);
        item.setProduto(refri);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("12.00"));
        item.setCompartilhado(true);
        itens.add(item);

        Comanda comanda = new Comanda();
        comanda.setId(1L);
        comanda.setMesa(mesa);
        comanda.setItens(itens);
        comanda.setRateios(new ArrayList<>());
        comanda.setStatus(StatusComanda.ABERTA);
        return comanda;
    }
}
