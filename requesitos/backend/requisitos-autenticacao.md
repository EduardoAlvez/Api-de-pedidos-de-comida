# Requisitos — Autenticação e Autorização

## RF039: Login
- **Endpoint:** `POST /login`
- **Body:** `{ "email": String, "senha": String }`
- **Resposta:** `200 OK` com `{ "token": String, "type": "Bearer" }`
- **Validações:**
  - Email e senha obrigatórios
  - Se credenciais inválidas → `401 Unauthorized`
- **Regras de negócio:**
  - Token JWT com `subject = email`, issuer = "API Pedidos E-commerce"
  - Token expira em 7 dias
  - Implementação: `TokenService` gera e valida tokens com HMAC-SHA256

## RF040: Roles (Perfis de Acesso)
| Role | Acesso |
|---|---|
| `CLIENTE` | Pedidos delivery, produtos públicos |
| `GARCOM` | Mesas, comandas, itens, rateio, Pix |
| `DONO_RESTAURANTE` | Tudo do próprio restaurante (produtos, mesas, comandas, pedidos, regiões) |

- `CLIENTE` vê apenas endpoints de pedido e produto
- `GARCOM` tem as mesmas permissões de `CLIENTE` + mesas/comandas
- `DONO_RESTAURANTE` tem permissões totais sobre o próprio restaurante

## RF041: Autorização por Restaurante (Isolamento de Dados)
- Cada usuário (GARCOM ou DONO) está vinculado a um restaurante
- Usuários **não podem** acessar dados de outros restaurantes
- Tentativa de acesso cruzado → `404 Not Found` (para não expor existência)
- O restaurante é resolvido automaticamente via `Usuario.getRestauranteVinculado()`

## RF042: Autorização via SecurityFilter
- `SecurityFilter` (OncePerRequestFilter) intercepta todas as requisições
- Fluxo:
  1. Extrai token do header `Authorization: Bearer <token>`
  2. Valida token (assinatura + expiração)
  3. Carrega `UserDetails` (email, senha, roles)
  4. Popula `SecurityContextHolder` com `UsernamePasswordAuthenticationToken`
  5. Se token inválido/ausente → `401 Unauthorized`
- Endpoints públicos (`/login`, `/webhook/mercadopago`, `/h2-console/**`) não exigem token

## RF043: Rotas Públicas
- `POST /login`
- `POST /API/V1/webhook/mercadopago`
- `GET /API/V1/produtos` (listar)
- `GET /API/V1/produtos/{id}` (buscar)
- `/h2-console/**` (apenas dev)

## RF044: Vincular Garçom ao Restaurante
- **Endpoint:** `POST /API/V1/garcons/vincular`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Body:** `{ "emailGarcom": String }`
- **Resposta:** `200 OK`
- **Regras de negócio:**
  - Garçom deve existir e ter role `GARCOM`
  - Garçom não pode estar vinculado a outro restaurante
  - Dono só pode vincular garçons ao próprio restaurante
