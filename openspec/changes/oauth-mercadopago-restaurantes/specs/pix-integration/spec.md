## MODIFIED Requirements

### Requirement: PixClient cria cobrança com token dinâmico
A interface `PixClient.criarCobranca()` agora aceita `accessToken` como parâmetro explícito. O token global do `MercadoPagoConfigSetup` se torna fallback quando `null`.

**Alterações:**
- Método `criarCobranca(valor, descricao)` → `criarCobranca(valor, descricao, accessToken)`
- Se `accessToken` for `null`, usa token global como fallback

#### Scenario: Criar cobrança com token do restaurante
- **WHEN** PixService chama `criarCobranca(47.90, "Comanda #2", tokenRestaurante)`
- **THEN** MercadoPagoPixClient usa `tokenRestaurante` no header `Authorization` e retorna QR Code

#### Scenario: Criar cobrança sem token (fallback)
- **WHEN** PixService chama `criarCobranca(47.90, "Comanda #2", null)` para restaurante não conectado
- **THEN** MercadoPagoPixClient usa `MercadoPagoConfigSetup.getAccessToken()` e retorna QR Code

---

### Requirement: PixService resolve token do restaurante
O `PixService.gerarQrCode()` DEVE buscar o `ConexaoMercadoPago` ativo do restaurante associado à comanda antes de criar a cobrança.

#### Scenario: Resolver token de restaurante conectado
- **WHEN** comanda tem mesa → mesa tem restaurante → restaurante tem `ConexaoMercadoPago` ativo e token válido
- **THEN** PixService passa `accessToken` do banco para `criarCobranca()`

#### Scenario: Resolver token de restaurante conectado com token expirado
- **WHEN** comanda tem restaurante → `ConexaoMercadoPago` ativo mas token expirado
- **THEN** PixService faz refresh do token, salva novo, e passa novo token para `criarCobranca()`

#### Scenario: Resolver token de restaurante não conectado
- **WHEN** comanda tem restaurante → nenhuma `ConexaoMercadoPago` ativa encontrada
- **THEN** PixService passa `null` para `criarCobranca()` (usa token global)

---

### Requirement: Comanda com restaurante não conectado funciona normalmente
Para restaurantes que ainda não conectaram conta MP, o fluxo de Pix DEVE continuar funcionando com o token global atual.

#### Scenario: Restaurante sem conexão MP
- **WHEN** garçom gera QR Code para comanda de restaurante sem `ConexaoMercadoPago`
- **THEN** sistema gera QR Code normalmente com o token global do properties
