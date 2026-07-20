# Requisitos — Integração PIX (Mercado Pago)

## RF019: Gerar QR Code PIX
- **Endpoint:** `POST /API/V1/comandas/{id}/pix`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Resposta:** `200 OK` com `PixResponseDTO` (qrCodeBase64, payloadCopiaCola, txId)
- **Validações:**
  - Comanda deve existir e estar `ABERTA` ou `AGUARDANDO_PIX`
  - Se comanda `PAGA` → `400 Bad Request` "Comanda já está paga"
  - Se valor total já foi totalmente rateado → `400 Bad Request`
- **Regras de negócio:**
  - Se comanda `ABERTA`: calcula valor = `valorTotal - soma(rateios)` e gera nova cobrança
  - Se comanda `AGUARDANDO_PIX`:
    - Se transação anterior está `PENDENTE`: retorna QR Code já gerado (idempotente)
    - Se transação anterior está `EXPIRADA`: gera nova cobrança
  - Status da comanda muda para `AGUARDANDO_PIX`
  - Cria registro `TransacaoPix` com status `PENDENTE`

## RF020: Resolver Token do Restaurante
- Antes de chamar a API do Mercado Pago, o sistema resolve qual token usar:
  1. Se o restaurante possui `ConexaoMercadoPago` ativa → usa o token do restaurante
  2. Se o token expirou → faz refresh automático via `refresh_token`
  3. Se não tem conexão própria → usa token global (fallback)
- A chamada à API do MP leva o token como parâmetro

## RF021: Webhook de Confirmação
- **Endpoint:** `POST /API/V1/pix/webhook`
- **Autenticação:** Nenhuma (público, validado por assinatura HMAC)
- **Body:** Payload do Mercado Pago com `txId` e status
- **Resposta:** `200 OK` (processado) | `401` (assinatura inválida) | `404` (txId não encontrado)
- **Regras de negócio:**
  - Valida assinatura HMAC do header `x-signature`
  - Se assinatura inválida → `401 Unauthorized`
  - Localiza `TransacaoPix` pelo `txId`
  - Se `txId` não encontrado → `404 Not Found`
  - Se status = "approved": atualiza `TransacaoPix.status` para `APROVADO`, comanda para `PAGA`
  - Se status = "rejected" ou "expired": atualiza `TransacaoPix.status` correspondente

## RF022: Consultar Status PIX
- **Endpoint:** `GET /API/V1/comandas/{id}/pix`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Resposta:** `200 OK` com status da transação e QR Code (se aplicável)
- **Validações:**
  - Comanda deve existir
  - Se comanda não possui `TransacaoPix` → `404 Not Found`

## RF023: Fechar Comanda (não permite PIX)
- **Endpoint:** `POST /API/V1/comandas/{id}/fechar`
- Se `formaPagamento = PIX` → `400 Bad Request` "Use o endpoint /pix para gerar QR Code"

## RF024: Credenciais
- **Modo produção:** Usar credenciais `APP_USR` (Access Token + Webhook Secret) definidas em `secrets.properties` (gitignored)
- **Modo desenvolvimento:** H2 em memória com `application-h2.properties`
- As credenciais são carregadas via `MercadoPagoConfigSetup`
