package com.ecommerce.pedido.controllers;

import com.ecommerce.pedido.dtos.PixResponseDTO;
import com.ecommerce.pedido.dtos.WebhookMercadoPagoDTO;
import com.ecommerce.pedido.services.PixService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/API/V1")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @PostMapping("/comandas/{id}/pix")
    public ResponseEntity<PixResponseDTO> gerarQrCode(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pixService.gerarQrCode(id));
    }

    @GetMapping("/comandas/{id}/pix")
    public ResponseEntity<PixResponseDTO> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(pixService.consultarStatus(id));
    }

    @PostMapping("/pix/webhook")
    public ResponseEntity<Map<String, String>> receberWebhook(
            @RequestBody WebhookMercadoPagoDTO payload,
            @RequestHeader("x-signature") String assinatura,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestParam("data.id") String dataId) {
        pixService.processarWebhook(payload, assinatura, xRequestId, dataId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
