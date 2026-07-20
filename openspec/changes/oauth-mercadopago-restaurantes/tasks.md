## 1. Configuração

- [ ] 1.1 Adicionar `mp-oauth.client-id`, `mp-oauth.client-secret`, `mp-oauth.redirect-uri` em `application-h2.properties`
- [ ] 1.2 Adicionar as mesmas props em `application-mysql.properties` e `application-postgre.properties`
- [ ] 1.3 Criar `MercadoPagoOAuthConfig.java` lendo as props do properties

## 2. Model e Repository

- [ ] 2.1 Criar entidade `ConexaoMercadoPago.java` em `models/` com campos: id, restaurante (FK), accessToken, refreshToken, expiresAt, mpUserId, mpSellerId, ativo, criadoEm, atualizadoEm
- [ ] 2.2 Adicionar relacionamento `@OneToOne` em `Restaurante.java` para `ConexaoMercadoPago`
- [ ] 2.3 Criar `ConexaoMercadoPagoRepository.java` com método `findByRestaurante_IdAndAtivoTrue(Long restauranteId)`

## 3. Service OAuth

- [ ] 3.1 Criar `OAuthMercadoPagoService.java` com método `gerarUrlAutorizacao()` que monta URL com `client_id`, `redirect_uri`, `response_type=code`, `state` (UUID)
- [ ] 3.2 Implementar storage de `state` em memória (`Map<String, Long> stateRestauranteMap`) para validar callback
- [ ] 3.3 Implementar `processarCallback(code, state)` que valida state, faz POST para `https://api.mercadopago.com/oauth/token` com `grant_type=authorization_code`, e salva `ConexaoMercadoPago`
- [ ] 3.4 Implementar `refreshToken(ConexaoMercadoPago)` que faz POST para `/oauth/token` com `grant_type=refresh_token` e atualiza o registro

## 4. Controller OAuth

- [ ] 4.1 Criar `OAuthMercadoPagoController.java` com `GET /API/V1/auth/mercadopago/url` (autenticado, role DONO_RESTAURANTE)
- [ ] 4.2 Implementar `GET /API/V1/auth/mercadopago/callback` (público, sem autenticação) com parâmetros `code` e `state`

## 5. Modificar PixClient Interface

- [ ] 5.1 Alterar `PixClient.criarCobranca(BigDecimal valor, String descricao)` para `criarCobranca(BigDecimal valor, String descricao, String accessToken)`
- [ ] 5.2 Atualizar todos os callers da interface (incluindo testes)

## 6. Modificar MercadoPagoPixClient

- [ ] 6.1 Alterar `criarCobranca()` para usar o parâmetro `accessToken` no header `Authorization`
- [ ] 6.2 Se `accessToken` for `null`, usar `config.getAccessToken()` como fallback

## 7. Modificar PixService

- [ ] 7.1 Injetar `RestauranteRepository` e `ConexaoMercadoPagoRepository` em `PixService`
- [ ] 7.2 Em `gerarQrCode()`, após carregar comanda, resolver restaurante via `comanda.getMesa().getRestaurante()`
- [ ] 7.3 Buscar `ConexaoMercadoPago` ativa do restaurante
- [ ] 7.4 Se token expirado, chamar `OAuthMercadoPagoService.refreshToken()` para renovar
- [ ] 7.5 Passar token resolvido (ou null) para `pixClient.criarCobranca()`

## 8. Segurança

- [ ] 8.1 Adicionar `/API/V1/auth/mercadopago/callback` às rotas públicas no `SecurityConfig.java`
- [ ] 8.2 Adicionar `/API/V1/auth/mercadopago/url` com role DONO_RESTAURANTE

## 9. Webhook mp-connect (desvinculação)

- [ ] 9.1 Adicionar lógica em `PixService.processarWebhook()` (ou novo endpoint) para tratar evento `mp-connect` com `action=disconnected`
- [ ] 9.2 Quando detectar desvinculação, marcar `ConexaoMercadoPago.ativo = false`

## 10. Testes

- [ ] 10.1 Criar testes unitários para `OAuthMercadoPagoService` (gerar URL, processar callback, refresh)
- [ ] 10.2 Atualizar `PixServiceTest` para mockar resolução de token (nova dependência)
- [ ] 10.3 Atualizar testes de `MercadoPagoPixClient` com novo parâmetro accessToken
- [ ] 10.4 Atualizar `PixClientTest` com nova assinatura do método
