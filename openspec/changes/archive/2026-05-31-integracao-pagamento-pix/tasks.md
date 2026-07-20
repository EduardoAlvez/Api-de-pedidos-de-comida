## 1. Configuração e dependências

- [x] 1.1 Adicionar dependência do SDK Mercado Pago no `pom.xml`
- [x] 1.2 Adicionar `mercado-pago.access-token` e `mercado-pago.webhook-secret` nos `application-{profile}.properties`
- [x] 1.3 Criar `MercadoPagoConfig` para ler propriedades e configurar o SDK do MP

## 2. Modelos e enums

- [x] 2.1 Adicionar `AGUARDANDO_PIX` ao enum `StatusComanda`
- [x] 2.2 Criar enum `StatusTransacaoPix` (AGUARDANDO, CONFIRMADO, EXPIROU)
- [x] 2.3 Criar entidade `TransacaoPix` com campos: id, comandaId, valor, qrCodeBase64, payloadCopiaCola, txId, status, dataCriacao, dataConfirmacao
- [x] 2.4 Criar `TransacaoPixRepository`

## 3. DTOs

- [x] 3.1 Criar `FecharComandaRequestDTO` com campo `formaPagamento` (FormaPagamento, opcional, default MAQUININHA)
- [x] 3.2 Criar `PixResponseDTO` com campos: valor, qrCodeBase64, payloadCopiaCola, txId, status, dataCriacao
- [x] 3.3 Criar `WebhookMercadoPagoDTO` para receber payload do webhook

## 4. Serviço Pix

- [x] 4.1 Criar `PixClient` (interface) com método `criarCobranca(BigDecimal valor)` e `processarWebhook(payload)`
- [x] 4.2 Criar `MercadoPagoPixClient` implementando `PixClient` usando SDK do Mercado Pago
- [x] 4.3 Criar `PixService` com métodos:

## 5. Controller

- [x] 5.1 Criar `PixController` com endpoints:
- [x] 5.2 Atualizar `ComandaController.fecharComanda()` para usar `FecharComandaRequestDTO` com `formaPagamento`
- [x] 5.3 Atualizar `ComandaService.fechar()` para aceitar `FormaPagamento` e validar se é PIX (deve usar /pix)

## 6. Validação e tratamento de erros

- [x] 6.1 Criar `ValidacaoAssinaturaException` para webhook com assinatura inválida
- [x] 6.2 Registrar handlers no `ResourceExceptionHandler` para novas exceções

## 7. Carga de dados (DataLoader)

- [x] 7.1 Atualizar `DataLoader` com exemplo de comanda com transação Pix

## 8. Testes

- [x] 8.1 Criar `PixServiceTest` com testes unitários (9 testes):
  - Geração de QR Code com sucesso (sem rateio, valor = total)
  - Geração de QR Code com rateio já pago (valor = saldo restante)
  - Geração de QR Code quando total já foi rateado (erro)
  - Comanda já PAGA retorna erro
  - Comanda já AGUARDANDO_PIX retorna QR Code existente
  - Webhook confirma pagamento com assinatura válida
  - Webhook com assinatura inválida retorna erro
  - Webhook com txId inexistente retorna erro
- [x] 8.2 Criar `PixControllerTest` com testes de integração (4 testes):
  - POST /comandas/{id}/pix retorna QR Code
  - GET /comandas/{id}/pix retorna status
  - POST /pix/webhook confirma pagamento
- [x] 8.3 Atualizar `ComandaControllerTest` e `ComandaServiceTest` para novo signature de `fechar()`

## 9. Documentação

- [x] 9.1 Adicionar novos endpoints no Swagger (springdoc já documenta automaticamente)
- [x] 9.2 Atualizar README.md com a nova funcionalidade de Pix
