## Context

Hoje a plataforma usa um token fixo do Mercado Pago (do dono da aplicação) para criar cobranças Pix via API de Orders (`/v1/orders`). O `MercadoPagoPixClient` lê o token do `MercadoPagoConfigSetup`, que carrega de `application-h2.properties`. Todas as comandas de todos os restaurantes geram QR Code usando a mesma credencial — o dinheiro cai todo na mesma conta.

Cada restaurante precisa poder conectar a própria conta do Mercado Pago para receber os pagamentos diretamente. O MP oferece OAuth para isso: o usuário autoriza nossa aplicação e recebemos um `access_token` exclusivo dele.

## Goals / Non-Goals

**Goals:**
- Dono do restaurante autorizar nossa plataforma via OAuth (um clique)
- Armazenar o token de cada restaurante no banco de dados
- Usar o token do restaurante ao criar cobranças Pix para comandas dele
- Renovar token automaticamente via `refresh_token` quando expirar
- Manter compatibilidade: restaurantes não conectados usam o token global (fallback)

**Non-Goals:**
- Não implementa split de pagamento (marketplace) — MP não oferece split nativo para Orders API
- Não implementa onboarding de múltiplos restaurantes simultâneos
- Não implementa frontend da tela de "Conectar MP" (apenas o endpoint)
- Não migra dados existentes — tokens globais continuam funcionando

## Decisions

**1. Nova entidade `ConexaoMercadoPago` separada de `Restaurante`**
- Alternativa: colocar campos direto em Restaurante
- Decisão: entidade separada porque `Restaurante` já tem muitos campos e a conexão MP tem ciclo de vida próprio (refresh, revogação). Facilita manutenção e consultas.

**2. Refresh automático síncrono no momento do uso**
- Alternativa: scheduler para renovar tokens antes de expirar
- Decisão: refresh sob demanda. Quando `PixService` buscar o token, verifica se expirou. Se sim, faz refresh com o `refresh_token` e salva o novo. Mais simples, sem complexidade de scheduler, e o refresh é rápido (~200ms).

**3. Armazenar token criptografado**
- Alternativa: texto puro na coluna
- Decisão: texto puro por enquanto (é um TCC). Em produção, usar `@Convert` com JPA `AttributeConverter` para criptografar com AES. Adicionar nota no design.

**4. Fallback para token global**
- Restaurantes que ainda não conectaram continuam funcionando com o token do `application-h2.properties`
- A `PixService` tenta primeiro `ConexaoMercadoPago` do restaurante. Se não existir, usa `MercadoPagoConfigSetup.getAccessToken()` como hoje.

**5. Endpoint de callback público (sem autenticação)**
- O MP redireciona o browser do usuário para nosso callback com um `code` temporário. O usuário já está logado no nosso sistema (sessão JWT). Validamos o state para prevenir CSRF.

## OAuth Flow

```
[Restaurante] → GET /API/V1/auth/mercadopago/url
                        ← {"url": "https://auth.mercadopago.com/authorization?...&state=X"}

[Restaurante] → (autoriza no MP, é redirecionado para)
             → GET /API/V1/auth/mercadopago/callback?code=...&state=...
                        → troca code por access_token + refresh_token (+ refresh automático)
                        ← {"mensagem": "Conta Mercado Pago conectada com sucesso"}
```

## Novos Endpoints

### `GET /API/V1/auth/mercadopago/url`
- Público (requer JWT do dono do restaurante)
- Parâmetros: `restauranteId` (path ou query)
- Gera `state` (UUID) salvo em sessão/cache para validar retorno
- Retorna `{"url": "https://auth.mercadopago.com/authorization?...&client_id=X&redirect_uri=Y&response_type=code&state=Z"}`

### `GET /API/V1/auth/mercadopago/callback`
- Público (MP redireciona o browser para cá)
- Parâmetros: `code`, `state`
- Valida `state` contra o gerado anteriormente
- Troca `code` por `access_token` e `refresh_token` via POST para `/oauth/token` do MP
- Salva `ConexaoMercadoPago` no banco
- Redireciona o browser para página de sucesso ou retorna JSON

## Modelo de Dados

### Nova Tabela: `conexao_mercadopago`

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | Long (PK) | Auto incremento |
| restaurante_id | Long (FK → Restaurante) | Restaurante dono do token |
| access_token | TEXT | Token de acesso MP (criptografar em prod) |
| refresh_token | TEXT | Token para renovar access_token |
| expires_at | TIMESTAMP | Quando o access_token expira |
| mp_user_id | VARCHAR | User ID do vendedor no MP |
| mp_seller_id | VARCHAR | Seller ID (se aplicável) |
| ativo | BOOLEAN | Se a conexão está ativa (false se revogou) |
| criado_em | TIMESTAMP | Data da conexão |
| atualizado_em | TIMESTAMP | Último refresh |

## Estrutura de Pacotes

```
└── com.ecommerce.pedido
    ├── configs
    │   └── MercadoPagoOAuthConfig.java   (client-id, client-secret, redirect-uri)
    ├── models
    │   └── ConexaoMercadoPago.java        (nova entidade)
    ├── repositories
    │   └── ConexaoMercadoPagoRepository.java
    ├── services
    │   ├── OAuthMercadoPagoService.java   (lógica OAuth)
    │   ├── PixClient.java                 (interface: método alterado)
    │   ├── MercadoPagoPixClient.java      (implementação: token dinâmico)
    │   └── PixService.java                (resolve token do restaurante)
    └── controllers
        └── OAuthMercadoPagoController.java  (url + callback)
```

## Mudanças na Interface `PixClient`

```java
public interface PixClient {
    CriarCobrancaResult criarCobranca(BigDecimal valor, String descricao, String accessToken);
}
```

O `accessToken` agora é passado como parâmetro. Se for `null`, usa o token global (fallback).

## Mudanças no `MercadoPagoPixClient.criarCobranca()`

- Linha 81: `config.getAccessToken()` → parâmetro `accessToken` (se null, usa `config.getAccessToken()` como fallback)
- A lógica de gerar QR Code, montar body, chamar API permanece idêntica

## Mudanças no `PixService.gerarQrCode()`

- Após buscar a `Comanda`, resolve o `Restaurante` associado (comanda → mesa → restaurante)
- Busca `ConexaoMercadoPago` ativa para o restaurante
  - Se existe e token expirou → faz refresh e salva novo token
  - Se existe e token válido → usa `accessToken` do banco
  - Se não existe → usa token global (`config.getAccessToken()`)
- Passa o token resolvido para `pixClient.criarCobranca()`

## Dependências Maven

Nenhuma nova dependência. A API do MP OAuth usa HTTP POST simples (`/oauth/token`) que o `HttpClient` do Java já atende.

## Risco / Trade-offs

- **[Segurança]** Token armazenado em plain text → Mitigação: adicionar `AttributeConverter` com AES antes de produção. Para TCC, plain text é aceitável.
- **[State CSRF]** URL de callback sem `state` permite ataques → Mitigação: gerar UUID `state` e validar no callback
- **[Token expirado]** Se refresh token expirar, restaurante precisa reconectar → Mitigação: `PixService` detecta falha no refresh e usa fallback global + loga warning
- **[Sem split nativo]** Orders API não suporta split → Se um dia precisar, migrar para Payments API ou modelo de "repasses manuais"
