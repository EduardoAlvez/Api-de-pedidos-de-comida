## Why

Hoje todos os pagamentos Pix via Mercado Pago usam um token fixo do dono da plataforma (`application-h2.properties`). O dinheiro cai todo na mesma conta. Cada restaurante precisa receber na própria conta do Mercado Pago. O OAuth permite que o dono do restaurante autorize nossa plataforma sem compartilhar senha, e cada um recebe seus pagamentos diretamente.

## What Changes

- Nova entidade `ConexaoMercadoPago` vinculada a `Restaurante` para armazenar access_token, refresh_token, expiry e mp_user_id
- Endpoint `GET /API/V1/auth/mercadopago/url` — gera URL de autorização OAuth do MP com escopos `read`, `write`, `offline_access`
- Endpoint `GET /API/V1/auth/mercadopago/callback` — recebe `authorization_code`, troca por token, salva no banco
- Refresh automático do token quando expirar (usando `refresh_token`)
- `PixClient.criarCobranca()` passa a receber `accessToken` como parâmetro (não lê mais do config global)
- `PixService` busca o `ConexaoMercadoPago` do restaurante da comanda antes de criar cobrança
- Webhook `mp-connect` para detectar desvinculações (restaurante revogou acesso)
- Token global do `MercadoPagoConfigSetup` vira **fallback** para restaurantes que ainda não conectaram

## Capabilities

### New Capabilities
- `oauth-mercadopago`: Fluxo completo de autorização OAuth com Mercado Pago. Geração de URL, callback com troca de código, refresh automático de token e persistência segura.

### Modified Capabilities
- `pix-integration`: PixClient aceita token dinâmico por transação. PixService resolve o token do restaurante antes de cada chamada à API do MP.

## Impact

- **Novo model**: `ConexaoMercadoPago` com campos `restaurante`, `accessToken`, `refreshToken`, `expiresAt`, `mpUserId`, `mpSellerId`
- **Interface alterada**: `PixClient.criarCobranca(valor, descricao, accessToken)` — terceiro parâmetro adicionado
- **Service alterado**: `PixService` injeta `RestauranteRepository` para resolver o token pelo restaurante da comanda
- **Novo controller**: `OAuthMercadoPagoController` com endpoints públicos de URL e callback
- **Novo service**: `OAuthMercadoPagoService` com lógica de troca de código, refresh e revogação
- **Webhook**: Registrar `mp-connect` no painel MP; endpoint novo para receber desvinculações
- **Nova config**: `mp-oauth.client-id`, `mp-oauth.client-secret`, `mp-oauth.redirect-uri` no properties
