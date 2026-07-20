## Why

A API já gerencia comandas e fechamento manual (maquininha/dinheiro), mas não tem integração com meios de pagamento digitais. Adicionar Pix via Mercado Pago permite que o cliente pague por QR Code e a comanda seja fechada automaticamente via webhook, sem o garçom precisar marcar manualmente.

## What Changes

- Nova entidade `TransacaoPix` para rastrear QR Codes gerados e confirmação via webhook
- Novo serviço `PixService` integrado com a API do Mercado Pago
- Novo `PixController` com endpoints para gerar QR Code e receber webhook
- Novo `StatusComanda.AGUARDANDO_PIX` — comanda aguardando confirmação do pagamento
- Atualização do `ComandaService.fechar()` para aceitar parâmetro de forma de pagamento (PIX, MAQUININHA, DINHEIRO)
- Configuração do token do Mercado Pago nos `application-{profile}.properties`
- Validação de assinatura do webhook via chave secreta

## Capabilities

### New Capabilities
- `pix-integration`: Geração de QR Code Pix via Mercado Pago, recebimento de webhook de confirmação, e atualização automática do status da comanda para PAGA

### Modified Capabilities
- `comandas`: endpoint `POST /comandas/{id}/fechar` passa a aceitar forma de pagamento; fluxo Pix altera status para AGUARDANDO_PIX (não PAGA direto)
- (*nenhuma outra spec existente é alterada em nível de requisitos*)

## Impact

- **Novas dependências**: SDK Mercado Pago Java (`com.mercadopago:sdk`)
- **Novas entidades**: `TransacaoPix` (id, comandaId, qrCodeBase64, payloadCopiaCola, txId, status, dataCriacao, dataConfirmacao)
- **Novos endpoints**:
  - `POST /API/V1/comandas/{id}/pix` — gera QR Code para a comanda
  - `POST /API/V1/pix/webhook` — recebe confirmação do Mercado Pago
  - `GET /API/V1/comandas/{id}/pix` — consulta status da transação Pix
- **Modificações**:
  - `StatusComanda`: adiciona `AGUARDANDO_PIX`
  - `ComandaService.fechar()`: recebe `FormaPagamento` como parâmetro
  - `ComandaController.fecharComanda()`: aceita `FormaPagamento` no body
- **Configuração**: `mercado-pago.access-token` e `mercado-pago.webhook-secret` nos properties de cada perfil
- **DataLoader**: seed de exemplo com transação Pix
