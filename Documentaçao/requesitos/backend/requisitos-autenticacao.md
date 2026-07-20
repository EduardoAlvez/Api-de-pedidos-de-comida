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
  - Token expira em **2 horas**
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
- Endpoints públicos (sem autenticação):
  - `POST /login`
  - `POST /API/V1/usuarios` (cadastro público)
  - `POST /API/V1/pix/webhook` (Mercado Pago)
  - `/h2-console/**` (apenas dev)
  - `/swagger-ui/**`, `/v3/api-docs/**` (documentação)

## RF043: Rotas Públicas
- `POST /login`
- `POST /API/V1/usuarios` (cadastro público — sempre cria com role CLIENTE)
- `POST /API/V1/pix/webhook` (notificação do Mercado Pago)
- `/h2-console/**` (apenas dev)
- `/swagger-ui/**`, `/v3/api-docs/**` (documentação)
- **Todos os demais endpoints exigem autenticação JWT**

## RF044: Vincular Garçom Existente ao Restaurante
- **Endpoint:** `POST /API/V1/restaurantes/{restauranteId}/garcons/vincular`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Body:** `{ "usuarioId": Long }`
- **Resposta:** `200 OK`
- **Regras de negócio:**
  - Garçom deve existir e ter role `GARCOM`
  - Garçom não pode estar vinculado a outro restaurante
  - Dono só pode vincular garçons ao próprio restaurante

## RF045: Dono Criar Garçom (em um único passo)
- **Endpoint:** `POST /API/V1/restaurantes/{restauranteId}/garcons`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Body:** `{ "nome": String, "email": String, "telefone": String, "senha": String }`
- **Resposta:** `201 Created` com `UsuarioResponseDTO` (id, nome, email, telefone, tipo=GARCOM)
- **Validações:**
  - `nome`, `email`, `telefone`, `senha` obrigatórios
  - Email não pode estar duplicado
  - Dono deve ser realmente dono do restaurante
- **Regras de negócio:**
  - O campo `tipo` é forçado para `GARCOM` pelo backend
  - O garçom é vinculado ao restaurante automaticamente
  - A senha é criptografada com BCrypt

## RF046: Cadastro Público Força CLIENTE (V-01)
- O campo `tipo` foi **removido** do `UsuarioRequestDTO`
- `UsuarioService.criar()` força `Role.CLIENTE` independente do que o cliente envia
- Cadastro de `GARCOM` e `DONO_RESTAURANTE` segue fluxos específicos (RF044, RF045)

## RF047: Autoidentificação em PUT/DELETE (V-02)
- Endpoints `PUT /API/V1/usuarios/{id}` e `DELETE /API/V1/usuarios/{id}` verificam que o **usuário autenticado é o mesmo sendo alterado/deletado**
- Se o ID do path não coincide com o ID do token → `400 Bad Request` "Você só pode alterar/deletar sua própria conta."

## RF048: Listagem de Usuários Restrita (V-03)
- O endpoint `GET /API/V1/usuarios` (listar todos) foi **removido**
- Cada usuário consulta seus próprios dados via `GET /API/V1/usuarios/{id}`
- Não há listagem pública de usuários

## RF049: Desvincular Garçom
- **Endpoint:** `DELETE /API/V1/restaurantes/{restauranteId}/garcons/{usuarioId}`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Resposta:** `204 No Content`
- **Regras de negócio:**
  - Garçom deve estar vinculado ao restaurante
  - Se não estiver vinculado → `400 Bad Request`

## RF050: Listar Garçons do Restaurante
- **Endpoint:** `GET /API/V1/restaurantes/{restauranteId}/garcons`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Resposta:** `200 OK` com `List<UsuarioResponseDTO>` (id, nome, email, tipo)
