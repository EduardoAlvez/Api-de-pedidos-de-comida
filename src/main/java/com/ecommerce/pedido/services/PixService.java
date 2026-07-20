package com.ecommerce.pedido.services;

import com.ecommerce.pedido.dtos.PixResponseDTO;
import com.ecommerce.pedido.dtos.WebhookMercadoPagoDTO;
import com.ecommerce.pedido.models.Comanda;
import com.ecommerce.pedido.models.ComandaRateio;
import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.models.TransacaoPix;
import com.ecommerce.pedido.models.Usuario;
import com.ecommerce.pedido.models.enums.StatusComanda;
import com.ecommerce.pedido.models.enums.StatusTransacaoPix;
import com.ecommerce.pedido.repositories.ComandaRepository;
import com.ecommerce.pedido.repositories.TransacaoPixRepository;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoAssinaturaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PixService {

    private static final Logger log = LoggerFactory.getLogger(PixService.class);

    private final PixClient pixClient;
    private final ComandaRepository comandaRepository;
    private final TransacaoPixRepository transacaoPixRepository;
    private final String webhookSecret;

    public PixService(PixClient pixClient,
                      ComandaRepository comandaRepository,
                      TransacaoPixRepository transacaoPixRepository,
                      com.ecommerce.pedido.configs.MercadoPagoConfigSetup config) {
        this.pixClient = pixClient;
        this.comandaRepository = comandaRepository;
        this.transacaoPixRepository = transacaoPixRepository;
        this.webhookSecret = config.getWebhookSecret();
    }

    @Transactional
    public PixResponseDTO gerarQrCode(Long comandaId, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Comanda não encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);

        if (comanda.getStatus() == StatusComanda.PAGA) {
            throw new ValidacaoNegocioException("Comanda já está paga.");
        }

        List<TransacaoPix> transacoesExistentes = transacaoPixRepository
                .findAllByComanda_Id(comandaId);

        TransacaoPix transacaoAguardando = transacoesExistentes.stream()
                .filter(t -> t.getStatus() == StatusTransacaoPix.AGUARDANDO)
                .max(Comparator.comparing(TransacaoPix::getDataCriacao))
                .orElse(null);

        if (transacaoAguardando != null) {
            if (transacaoAguardando.getDataCriacao() != null
                    && Duration.between(transacaoAguardando.getDataCriacao(), LocalDateTime.now()).toMinutes() < 15) {
                return toResponseDTO(transacaoAguardando);
            }
            transacaoAguardando.setStatus(StatusTransacaoPix.EXPIROU);
            transacaoPixRepository.save(transacaoAguardando);
        }

        BigDecimal saldoRestante = calcularSaldoRestante(comanda);

        if (saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoNegocioException("Comanda já foi totalmente paga via rateio.");
        }

        PixClient.CriarCobrancaResult resultado = pixClient.criarCobranca(
                saldoRestante, "Comanda #" + comandaId);

        TransacaoPix transacao = new TransacaoPix();
        transacao.setComanda(comanda);
        transacao.setValor(saldoRestante);
        transacao.setQrCodeBase64(resultado.qrCodeBase64());
        transacao.setPayloadCopiaCola(resultado.payloadCopiaCola());
        transacao.setTxId(resultado.txId());
        transacao.setStatus(StatusTransacaoPix.AGUARDANDO);
        transacao.setDataCriacao(LocalDateTime.now());

        transacaoPixRepository.save(transacao);

        comanda.setStatus(StatusComanda.AGUARDANDO_PIX);
        comandaRepository.save(comanda);

        return toResponseDTO(transacao);
    }

    public PixResponseDTO consultarStatus(Long comandaId, Usuario usuarioLogado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Comanda nao encontrada."));
        validarComandaRestaurante(comanda, usuarioLogado);
        List<TransacaoPix> transacoes = transacaoPixRepository
                .findAllByComanda_Id(comandaId);
        TransacaoPix transacao = transacoes.stream()
                .max(Comparator.comparing(TransacaoPix::getDataCriacao))
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Nenhuma transação Pix encontrada para esta comanda."));
        return toResponseDTO(transacao);
    }

    @Transactional
    public void processarWebhook(WebhookMercadoPagoDTO payload, String assinatura,
                                  String xRequestId, String dataId) {
        if (assinatura == null || assinatura.isBlank()) {
            throw new ValidacaoAssinaturaException("Webhook sem assinatura.");
        }
        if (!validarAssinatura(dataId, xRequestId, assinatura)) {
            throw new ValidacaoAssinaturaException("Assinatura do webhook inválida.");
        }

        if (payload.getAction() == null || !payload.getAction().equals("order.processed")) {
            return;
        }

        String orderId = payload.getData() != null ? payload.getData().getId() : null;
        if (orderId == null) {
            throw new ValidacaoNegocioException("orderId não encontrado no payload.");
        }

        TransacaoPix transacao = transacaoPixRepository.findByTxId(orderId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Transação não encontrada."));

        if (transacao.getStatus() == StatusTransacaoPix.CONFIRMADO) {
            return;
        }

        transacao.setStatus(StatusTransacaoPix.CONFIRMADO);
        transacao.setDataConfirmacao(LocalDateTime.now());
        transacaoPixRepository.save(transacao);

        Comanda comanda = transacao.getComanda();
        comanda.setStatus(StatusComanda.PAGA);
        comanda.setDataFechamento(LocalDateTime.now());
        comandaRepository.save(comanda);

        List<Comanda> comandasDaMesa = comandaRepository
                .findAllByMesa_IdOrderByDataAberturaDesc(comanda.getMesa().getId());
        long abertas = comandasDaMesa.stream()
                .filter(c -> c.getStatus() == StatusComanda.ABERTA
                        || c.getStatus() == StatusComanda.AGUARDANDO_PIX)
                .count();

        if (abertas == 0) {
            comanda.getMesa().setStatus(
                    com.ecommerce.pedido.models.enums.StatusMesa.LIVRE);
        }
    }

    public BigDecimal calcularSaldoRestante(Comanda comanda) {
        BigDecimal totalRateios = comanda.getRateios().stream()
                .map(ComandaRateio::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return comanda.getValorTotal().subtract(totalRateios);
    }

    private boolean validarAssinatura(String dataId, String xRequestId, String assinatura) {
        try {
            String ts = "";
            String v1 = "";

            for (String part : assinatura.split(",")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    if (key.equals("ts")) {
                        ts = value;
                    } else if (key.equals("v1")) {
                        v1 = value;
                    }
                }
            }

            if (ts.isEmpty() || v1.isEmpty()) {
                return false;
            }

            String dataIdLower = dataId != null ? dataId.toLowerCase() : "";
            String reqId = xRequestId != null ? xRequestId : "";
            String template = "id:" + dataIdLower + ";request-id:" + reqId + ";ts:" + ts + ";";

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(
                    webhookSecret.getBytes(), "HmacSHA256");
            mac.init(secret);
            byte[] hash = mac.doFinal(template.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString().equals(v1);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    private void validarComandaRestaurante(Comanda comanda, Usuario usuarioLogado) {
        Restaurante restauranteVinculado = usuarioLogado.getRestauranteVinculado();
        if (restauranteVinculado == null) {
            throw new ValidacaoNegocioException("Usuario nao vinculado a nenhum restaurante.");
        }
        if (!restauranteVinculado.getId().equals(comanda.getMesa().getRestaurante().getId())) {
            throw new EntidadeNaoEncontradaException("Comanda nao encontrada.");
        }
    }

    private PixResponseDTO toResponseDTO(TransacaoPix transacao) {
        PixResponseDTO dto = new PixResponseDTO();
        dto.setId(transacao.getId());
        dto.setComandaId(transacao.getComanda().getId());
        dto.setValor(transacao.getValor());
        dto.setQrCodeBase64(transacao.getQrCodeBase64());
        dto.setPayloadCopiaCola(transacao.getPayloadCopiaCola());
        dto.setTxId(transacao.getTxId());
        dto.setStatus(transacao.getStatus());
        dto.setDataCriacao(transacao.getDataCriacao());
        dto.setDataConfirmacao(transacao.getDataConfirmacao());
        return dto;
    }
}
