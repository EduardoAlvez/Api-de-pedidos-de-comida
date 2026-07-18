package com.ecommerce.pedido.services;

import com.ecommerce.pedido.configs.MercadoPagoConfigSetup;
import com.ecommerce.pedido.dtos.PixResponseDTO;
import com.ecommerce.pedido.dtos.WebhookMercadoPagoDTO;
import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.Role;
import com.ecommerce.pedido.models.enums.StatusComanda;
import com.ecommerce.pedido.models.enums.StatusMesa;
import com.ecommerce.pedido.models.enums.StatusTransacaoPix;
import com.ecommerce.pedido.repositories.ComandaRepository;
import com.ecommerce.pedido.repositories.TransacaoPixRepository;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoAssinaturaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Feature("Pix")
@Story("Service")
class PixServiceTest extends BaseServiceTest {

    private PixClient pixClient;
    private ComandaRepository comandaRepository;
    private TransacaoPixRepository transacaoPixRepository;
    private PixService pixService;

    private Usuario usuarioLogado;
    private Comanda comanda;
    private Mesa mesa;
    private Restaurante restaurante;
    private PixClient.CriarCobrancaResult resultadoSucesso;

    @BeforeEach
    void setUp() {
        pixClient = mock(PixClient.class);
        comandaRepository = mock(ComandaRepository.class);
        transacaoPixRepository = mock(TransacaoPixRepository.class);

        MercadoPagoConfigSetup config = mock(MercadoPagoConfigSetup.class);
        when(config.getWebhookSecret()).thenReturn("test-secret");

        pixService = new PixService(pixClient, comandaRepository,
                transacaoPixRepository, config);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");

        usuarioLogado = new Usuario();
        usuarioLogado.setId(2L);
        usuarioLogado.setTipo(Role.GARCOM);
        usuarioLogado.setRestauranteTrabalho(restaurante);

        mesa = new Mesa();
        mesa.setId(1L);
        mesa.setStatus(StatusMesa.OCUPADA);
        mesa.setRestaurante(restaurante);

        comanda = new Comanda();
        comanda.setId(1L);
        comanda.setMesa(mesa);
        comanda.setValorTotal(new BigDecimal("60.00"));
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setItens(new ArrayList<>());
        comanda.setRateios(new ArrayList<>());

        resultadoSucesso = new PixClient.CriarCobrancaResult(
                "iVBORw0KGgoAAAANSUhEUgAA...",
                "00020126580014br.gov.bcb.pix0136example@test.com5204000053039865404",
                "TX-12345"
        );
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    void deveGerarQrCode_quandoComandaEstaAberta() {
        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(transacaoPixRepository.findAllByComanda_Id(1L)).thenReturn(List.of());
        when(pixClient.criarCobranca(new BigDecimal("60.00"), "Comanda #1"))
                .thenReturn(resultadoSucesso);
        when(transacaoPixRepository.save(any(TransacaoPix.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(comandaRepository.save(any(Comanda.class)))
                .thenAnswer(i -> i.getArgument(0));

        PixResponseDTO response = pixService.gerarQrCode(1L, usuarioLogado);

        assertNotNull(response);
        assertEquals("TX-12345", response.getTxId());
        assertEquals(StatusTransacaoPix.AGUARDANDO, response.getStatus());
        assertEquals(new BigDecimal("60.00"), response.getValor());
        assertEquals(StatusComanda.AGUARDANDO_PIX, comanda.getStatus());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveGerarQrCodeComValorParcial_quandoHaRateio() {
        ComandaRateio rateio = new ComandaRateio();
        rateio.setValorPago(new BigDecimal("20.00"));
        comanda.setRateios(List.of(rateio));

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(transacaoPixRepository.findAllByComanda_Id(1L)).thenReturn(List.of());
        when(pixClient.criarCobranca(new BigDecimal("40.00"), "Comanda #1"))
                .thenReturn(resultadoSucesso);
        when(transacaoPixRepository.save(any(TransacaoPix.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(comandaRepository.save(any(Comanda.class)))
                .thenAnswer(i -> i.getArgument(0));

        PixResponseDTO response = pixService.gerarQrCode(1L, usuarioLogado);

        assertNotNull(response);
        assertEquals(new BigDecimal("40.00"), response.getValor());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveLancarExcecao_quandoComandaTotalmenteRateada() {
        ComandaRateio rateio = new ComandaRateio();
        rateio.setValorPago(new BigDecimal("60.00"));
        comanda.setRateios(List.of(rateio));

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));

        assertThrows(ValidacaoNegocioException.class,
                () -> pixService.gerarQrCode(1L, usuarioLogado));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveLancarExcecao_quandoComandaJaPaga() {
        comanda.setStatus(StatusComanda.PAGA);

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));

        assertThrows(ValidacaoNegocioException.class,
                () -> pixService.gerarQrCode(1L, usuarioLogado));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveRetornarQrCodeExistente_quandoComandaAguardandoPix() {
        TransacaoPix existente = new TransacaoPix();
        existente.setId(1L);
        existente.setComanda(comanda);
        existente.setTxId("TX-12345");
        existente.setValor(new BigDecimal("60.00"));
        existente.setStatus(StatusTransacaoPix.AGUARDANDO);
        existente.setDataCriacao(LocalDateTime.now());

        comanda.setStatus(StatusComanda.AGUARDANDO_PIX);

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(transacaoPixRepository.findAllByComanda_Id(1L)).thenReturn(List.of(existente));

        PixResponseDTO response = pixService.gerarQrCode(1L, usuarioLogado);

        assertNotNull(response);
        assertEquals("TX-12345", response.getTxId());
        verify(pixClient, never()).criarCobranca(any(), any());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveConfirmarPagamento_quandoWebhookValido() {
        TransacaoPix transacao = new TransacaoPix();
        transacao.setId(1L);
        transacao.setComanda(comanda);
        transacao.setTxId("ORD12345TEST");
        transacao.setStatus(StatusTransacaoPix.AGUARDANDO);

        when(transacaoPixRepository.findByTxId("ORD12345TEST"))
                .thenReturn(Optional.of(transacao));
        when(transacaoPixRepository.save(any(TransacaoPix.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(comandaRepository.save(any(Comanda.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(comandaRepository.findAllByMesa_IdOrderByDataAberturaDesc(1L))
                .thenReturn(List.of(comanda));

        String dataId = "ORD12345TEST";
        String xRequestId = "req-12345";
        String ts = "1742505638683";
        String v1 = gerarAssinaturaHex("test-secret", dataId, xRequestId, ts);
        String assinatura = "ts=" + ts + ",v1=" + v1;

        WebhookMercadoPagoDTO payload = new WebhookMercadoPagoDTO();
        payload.setAction("order.processed");
        WebhookMercadoPagoDTO.Data data = new WebhookMercadoPagoDTO.Data();
        data.setId(dataId);
        payload.setData(data);

        assertDoesNotThrow(() ->
                pixService.processarWebhook(payload, assinatura, xRequestId, dataId));

        assertEquals(StatusTransacaoPix.CONFIRMADO, transacao.getStatus());
        assertEquals(StatusComanda.PAGA, comanda.getStatus());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveConsultarStatus_quandoTransacaoExiste() {
        TransacaoPix transacao = new TransacaoPix();
        transacao.setId(1L);
        transacao.setComanda(comanda);
        transacao.setTxId("TX-12345");
        transacao.setValor(new BigDecimal("60.00"));
        transacao.setStatus(StatusTransacaoPix.CONFIRMADO);
        transacao.setDataConfirmacao(LocalDateTime.now());

        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(transacaoPixRepository.findAllByComanda_Id(1L))
                .thenReturn(List.of(transacao));

        PixResponseDTO response = pixService.consultarStatus(1L, usuarioLogado);

        assertNotNull(response);
        assertEquals(StatusTransacaoPix.CONFIRMADO, response.getStatus());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    void deveLancarExcecao_quandoConsultarStatusSemTransacao() {
        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));
        when(transacaoPixRepository.findAllByComanda_Id(1L))
                .thenReturn(List.of());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> pixService.consultarStatus(1L, usuarioLogado));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void deveRejeitarWebhook_comAssinaturaInvalida() {
        String dataId = "ORD12345TEST";
        String xRequestId = "req-12345";
        String assinatura = "ts=1742505638683,v1=assinatura-invalida";

        WebhookMercadoPagoDTO payload = new WebhookMercadoPagoDTO();
        payload.setAction("order.processed");
        WebhookMercadoPagoDTO.Data data = new WebhookMercadoPagoDTO.Data();
        data.setId(dataId);
        payload.setData(data);

        assertThrows(ValidacaoAssinaturaException.class,
                () -> pixService.processarWebhook(payload, assinatura, xRequestId, dataId));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    void deveCalcularSaldoRestante() {
        ComandaRateio rateio = new ComandaRateio();
        rateio.setValorPago(new BigDecimal("15.50"));
        comanda.setRateios(List.of(rateio));

        BigDecimal saldo = pixService.calcularSaldoRestante(comanda);

        assertEquals(new BigDecimal("44.50"), saldo);
    }

    private String gerarAssinaturaHex(String secret, String dataId, String xRequestId, String ts) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(key);
            String template = "id:" + dataId.toLowerCase() + ";request-id:" + xRequestId + ";ts:" + ts + ";";
            byte[] hash = mac.doFinal(template.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Erro ao gerar HMAC", e);
        }
    }
}
