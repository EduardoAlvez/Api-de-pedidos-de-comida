## ADDED Requirements

### Requirement: Dono do restaurante gera URL de autorização
O sistema SHALL expor endpoint `GET /API/V1/auth/mercadopago/url` que retorna a URL de autorização OAuth do Mercado Pago para o dono do restaurante autorizar nossa aplicação.

#### Scenario: Gerar URL com sucesso
- **WHEN** dono do restaurante faz GET em `/API/V1/auth/mercadopago/url` com JWT válido
- **THEN** sistema retorna `{"url": "https://auth.mercadopago.com/authorization?...&client_id=...&redirect_uri=...&response_type=code&state=..."}`

#### Scenario: Tentar gerar URL sem autenticação
- **WHEN** usuário não autenticado faz GET em `/API/V1/auth/mercadopago/url`
- **THEN** sistema retorna 401

#### Scenario: Tentar gerar URL com role CLIENTE
- **WHEN** usuário com role CLIENTE faz GET em `/API/V1/auth/mercadopago/url`
- **THEN** sistema retorna 403

---

### Requirement: Callback recebe authorization_code e troca por token
O sistema SHALL expor endpoint `GET /API/V1/auth/mercadopago/callback` que recebe `code` e `state`, valida o `state`, e troca o `code` por `access_token` e `refresh_token` via POST para `https://api.mercadopago.com/oauth/token`.

#### Scenario: Callback com code e state válidos
- **WHEN** MP redireciona para `/API/V1/auth/mercadopago/callback?code=AUTHORIZATION_CODE&state=UUID_VALIDO`
- **THEN** sistema valida `state`, troca `code` por token, salva `ConexaoMercadoPago` no banco, redireciona para página de sucesso

#### Scenario: Callback com state inválido
- **WHEN** MP redireciona para callback com `state` que não corresponde ao gerado
- **THEN** sistema retorna 400 com mensagem "state inválido"

#### Scenario: Callback com code expirado
- **WHEN** MP redireciona com `code` expirado (código temporário de 10 min)
- **THEN** sistema retorna 400 com mensagem "código de autorização inválido"

---

### Requirement: Conexão Mercado Pago é persistida
O sistema SHALL salvar o `ConexaoMercadoPago` associado ao restaurante com `accessToken`, `refreshToken`, `expiresAt`, `mpUserId`, `mpSellerId` e flag `ativo`.

#### Scenario: Salvar conexão após callback bem-sucedido
- **WHEN** callback troca `code` por token com sucesso
- **THEN** sistema persiste registro em `conexao_mercadopago` com dados completos e `ativo = true`

#### Scenario: Ver conexão de restaurante
- **WHEN** dono do restaurante busca status da conexão
- **THEN** sistema retorna se está conectado, mp_user_id e data da conexão

---

### Requirement: Refresh automático do access_token
O sistema SHALL renovar o `access_token` usando `refresh_token` quando detectar que expirou durante o uso.

#### Scenario: Token expirado ao criar cobrança
- **WHEN** PixService tenta criar cobrança e descobre `expiresAt < now`
- **THEN** sistema faz POST para `/oauth/token` com `grant_type=refresh_token`, salva novo `access_token` + `expiresAt`, e usa o novo token para criar a cobrança

#### Scenario: Refresh token também expirado
- **WHEN** tentativa de refresh retorna erro 400 (refresh_token inválido)
- **THEN** sistema marca `ativo = false` na conexão, loga warning, usa token global como fallback

---

### Requirement: Webhook mp-connect detecta desvinculação
O sistema SHALL expor endpoint no webhook existente (ou novo) para receber notificações `mp-connect` quando o restaurante revogar o acesso.

#### Scenario: Revogação de token detectada
- **WHEN** MP envia webhook `mp-connect` com `action=disconnected` para o restaurante
- **THEN** sistema marca `ativo = false` e loga o evento
