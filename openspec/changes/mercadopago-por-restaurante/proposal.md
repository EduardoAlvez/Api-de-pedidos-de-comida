## Why

Atualmente o Mercado Pago é configurado globalmente via variável de ambiente (`MP_ACCESS_TOKEN`). Isso impede que múltiplos restaurantes usem contas próprias do Mercado Pago — todos compartilham o mesmo token, o que é inviável para um sistema multi-tenant.

Cada dono de restaurante deve poder configurar suas próprias credenciais do Mercado Pago, para que os pagamentos PIX caiam diretamente na conta do restaurante, não numa conta centralizada.

## What Changes

- **Remover** `MercadoPagoConfigSetup.java` (config global via env vars)
- **Adicionar** `mp_access_token` e `mp_webhook_secret` na entidade `Restaurante` (nullable)
- **Criar** `MercadoPagoConfigDTO.java` para request/response
- **Adicionar** endpoint `PUT /API/V1/restaurantes/{id}/mercado-pago` para dono configurar credenciais
- **Adicionar** endpoint `DELETE /API/V1/restaurantes/{id}/mercado-pago` para remover
- **Alterar** `MercadoPagoPixClient.criarCobranca()` para receber `accessToken` como parâmetro (em vez de usar o global)
- **Alterar** webhook de `POST /pix/webhook` para `POST /pix/webhook/{restauranteId}` — assim o backend sabe qual `webhookSecret` usar para validar a assinatura
- **Alterar** `ComandaService` / `PixService` para obter o token do restaurante via `comanda.getMesa().getRestaurante().getMpAccessToken()`
- **Proteger** endpoints de PIX: se restaurante não tem token configurado, retornar 400 "PIX não disponível para este restaurante"

### Fluxo final esperado

1. CLIENTE cadastra conta e depois cria restaurante (vira DONO)
2. DONO acessa tela web de configurações e informa Access Token + Webhook Secret do Mercado Pago
3. Ao gerar QR Code PIX, o backend usa o token do restaurante da comanda
4. Webhook do Mercado Pago aponta para `https://api.fly.dev/pix/webhook/{restauranteId}` e o backend valida com o secret daquele restaurante
5. Se o dono nunca configurou, PIX simplesmente não aparece como opção de pagamento

## Capabilities

### New Capabilities
- `mercadopago-por-restaurante`: Configurar credenciais do Mercado Pago por restaurante

### Modified Capabilities
- `pix-integration`: Webhook passa a incluir `restauranteId` no path; token não é mais global

## Impact

- **Modelos**: `Restaurante` ganha 2 campos nullable (`mpAccessToken`, `mpWebhookSecret`)
- **DTOs**: Novo `MercadoPagoConfigDTO`
- **Controller**: `RestauranteController` ganha 2 novos endpoints; `PixController` altera rota do webhook
- **Services**: `MercadoPagoPixClient` muda assinatura; `PixService` passa a resolver token por restaurante
- **Config**: `MercadoPagoConfigSetup.java` é removido
- **Database**: Migração para adicionar colunas na tabela `restaurantes`
- **Testes**: Atualizar `PixControllerTest` e `PixServiceTest` para usar token por restaurante
