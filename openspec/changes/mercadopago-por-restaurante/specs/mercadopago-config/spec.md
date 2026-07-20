## ADDED Requirements

### Requirement: Dono pode configurar credenciais Mercado Pago do restaurante
O sistema SHALL permitir que o dono do restaurante configure o Access Token e Webhook Secret do Mercado Pago.
- Endpoint: `PUT /API/V1/restaurantes/{restauranteId}/mercado-pago`
- Autenticação: JWT obrigatório (role DONO_RESTAURANTE)
- Body: `{"accessToken": String, "webhookSecret": String}`
- Retorna `200 OK` com os campos atualizados (token mascarado parcialmente)
- Valida que o usuário logado é dono do restaurante

#### Scenario: Dono configura Mercado Pago com sucesso
- **WHEN** dono autenticado envia PUT para `/API/V1/restaurantes/1/mercado-pago` com `{"accessToken": "APP_USR-xxx", "webhookSecret": "abc123"}`
- **THEN** sistema retorna `200 OK` e o restaurante passa a ter PIX disponível

#### Scenario: Não-dono tenta configurar
- **WHEN** usuário sem role DONO_RESTAURANTE tenta configurar
- **THEN** sistema retorna `403 Forbidden`

#### Scenario: Dono de outro restaurante tenta configurar
- **WHEN** dono do restaurante 2 tenta configurar o restaurante 1
- **THEN** sistema retorna `404 Not Found`

### Requirement: Dono pode remover credenciais Mercado Pago
O sistema SHALL permitir que o dono remova as credenciais, desabilitando o PIX.
- Endpoint: `DELETE /API/V1/restaurantes/{restauranteId}/mercado-pago`
- Retorna `204 No Content`
- Após remover, o PIX deixa de ser oferecido para as mesas deste restaurante

### Requirement: PIX só disponível se token configurado
O sistema SHALL verificar se o restaurante possui `mpAccessToken` antes de processar um pagamento PIX.
- Endpoint: `POST /API/V1/comandas/{id}/pix`
- Se `restaurante.mpAccessToken == null` → `400 Bad Request` "PIX não disponível para este restaurante"

#### Scenario: Gerar PIX sem token configurado
- **WHEN** garçom tenta gerar QR Code PIX em restaurante sem credenciais
- **THEN** sistema retorna `400 Bad Request`

### Requirement: Webhook identifica restaurante pelo path
O sistema SHALL alterar a rota do webhook para incluir o `restauranteId`.
- Endpoint: `POST /API/V1/pix/webhook/{restauranteId}`
- O backend usa o `restauranteId` para obter o `mpWebhookSecret` e validar a assinatura HMAC
- Após validação, confirma a transação normalmente

#### Scenario: Webhook válido com restauranteId correto
- **WHEN** Mercado Pago envia notificação para `/API/V1/pix/webhook/1` com assinatura válida
- **THEN** sistema valida usando o secret do restaurante 1 e confirma o pagamento

#### Scenario: Webhook para restaurante inexistente
- **WHEN** Mercado Pago envia notificação para `/API/V1/pix/webhook/999`
- **THEN** sistema retorna `404 Not Found`

### MODIFIED Requirements

### Requirement: MercadoPagoPixClient recebe token como parâmetro
O método `criarCobranca(valor, descricao, accessToken)` SHALL receber o token explicitamente em vez de usar o token global.
- `MercadoPagoConfigSetup.java` é removido
- `MercadoPagoPixClient` não depende mais de `MercadoPagoConfigSetup`
- O token é obtido de `comanda.mesa.restaurante.mpAccessToken` pelo caller

### Requirement: PixService resolve token por restaurante
`PixService` SHALL buscar o token do restaurante ao processar pagamentos e validações de webhook.
- Ao gerar QR Code: busca `comanda.mesa.restaurante.mpAccessToken`
- Ao validar webhook: busca `restauranteRepository.findById(restauranteId).mpWebhookSecret`
