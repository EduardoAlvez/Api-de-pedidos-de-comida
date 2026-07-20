# Plano de Testes — Vincular Garcom ao Restaurante

## 1. Atualização do DataLoader

- Vincular `garcom1` (email: garcom@email.com, id: 4) ao `restaurante1` (Sabor Brasileiro)
- Adicionar `garcom2` (email: garcom2@email.com, id: 5) SEM vínculo (para testes de garcom sem restaurante)

## 2. Novos Tokens no BaseControllerTest

| Método | Usuário | Id | Email | Role |
|---|---|---|---|---|
| `tokenDono1()` | Maria Restaurante | 2L | dono1@email.com | DONO_RESTAURANTE |
| `tokenDono2()` | Carlos Restaurante | 3L | dono2@email.com | DONO_RESTAURANTE |
| `tokenGarcomSemVinculo()` | garcom2 | 5L | garcom2@email.com | GARCOM |

## 3. Testes Unitários — VinculoGarcomServiceTest

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `deveVincularGarcom_comDonoValido()` | Dono vincula GARCOM ao seu restaurante | Sucesso |
| `deveLancarExcecao_quandoDonoNaoPertenceAoRestaurante()` | Dono tenta vincular GARCOM a restaurante alheio | AcessoRestauranteException |
| `deveLancarExcecao_quandoUsuarioNaoEGarcom()` | Dono tenta vincular usuário CLIENTE | ValidacaoNegocioException |
| `deveLancarExcecao_quandoGarcomJaVinculado()` | Dono tenta vincular GARCOM já vinculado | ValidacaoNegocioException |
| `deveDesvincularGarcom_comDonoValido()` | Dono desvincula GARCOM do seu restaurante | Sucesso |
| `deveLancarExcecao_quandoGarcomNaoVinculadoAoRestaurante()` | Dono tenta desvincular GARCOM não vinculado | ValidacaoNegocioException |

## 4. Testes de Integração — GarcomControllerTest

| Teste | Método | Cenário | HTTP esperado |
|---|---|---|---|
| `deveVincularGarcom_donoRetornar200()` | POST | Dono1 vincula garcom2 ao restaurante1 | 200 OK |
| `deveRetornar403_quandoClienteTentaVincular()` | POST | Cliente tenta vincular | 403 FORBIDDEN |
| `deveRetornar403_quandoDonoOutroRestauranteTentaVincular()` | POST | Dono2 tenta vincular ao restaurante1 | 403 FORBIDDEN |
| `deveDesvincularGarcom_donoRetornar204()` | DELETE | Dono1 desvincula garcom | 204 NO CONTENT |
| `deveListarGarcons_retornar200()` | GET | Lista garcons de restaurante1 | 200 OK |

## 5. Testes de Integração — IsolamentoDadosTest

### 8.2 Garcom acessa apenas dados do seu restaurante

| Teste | Método | Cenário | HTTP |
|---|---|---|---|
| `garcom_deveListarProdutos_doSeuRestaurante()` | GET /produtos/restaurante/1 | Garcom vinculado ao rest1 vê produtos | 200 |
| `garcom_naoDeveListarProdutos_deOutroRestaurante()` | GET /produtos/restaurante/2 | Garcom tenta ver produtos do rest2 | 404 |
| `garcom_deveListarMesas_doSeuRestaurante()` | GET /mesas | Garcom vinculado lista mesas | 200 |
| `garcom_naoDeveCriarMesa_emOutroRestaurante()` | POST /mesas | Garcom cria mesa no rest2 (via body) | 403 |
| `garcom_deveListarComandas_doSeuRestaurante()` | GET /comandas?mesaId=1 | Garcom lista comandas da sua mesa | 200 |
| `garcom_naoDeveAcessarComanda_deOutroRestaurante()` | GET /comandas/{id} | Garcom busca comanda de outro restaurante | 404 |

### 8.3 Dono acessa apenas dados do seu restaurante

| Teste | Método | Cenário | HTTP |
|---|---|---|---|
| `dono_deveListarProdutos_doSeuRestaurante()` | GET /produtos/restaurante/1 | Dono1 vê produtos do rest1 | 200 |
| `dono_naoDeveListarProdutos_deOutroRestaurante()` | GET /produtos/restaurante/2 | Dono1 tenta ver produtos do rest2 | 404 |
| `dono_deveListarMesas_doSeuRestaurante()` | GET /mesas | Dono1 vê mesas do rest1 | 200 |

### 8.4 CLIENTE continua sem restrição

| Teste | Método | Cenário | HTTP |
|---|---|---|---|
| `cliente_deveListarProdutos_deQualquerRestaurante()` | GET /produtos/restaurante/2 | Cliente vê produtos do rest2 | 200 |
| `cliente_deveBuscarPedido_semRestricao()` | GET /pedidos/1 | Cliente busca pedido (criação tem validação complexa; busca é suficiente) | 200 |

### 8.5 Garcom sem vinculo tentando operacoes

| Teste | Método | Cenário | HTTP |
|---|---|---|---|
| `garcomSemVinculo_naoDeveListarMesas()` | GET /mesas | Garcom sem vinculo tenta listar mesas | 400 |
| `garcomSemVinculo_naoDeveCriarComanda()` | POST /mesas/1/comandas | Garcom sem vinculo tenta criar comanda | 400 |
| `garcomSemVinculo_naoDeveListarProdutos()` | GET /produtos/restaurante/1 | Garcom sem vinculo tenta listar produtos | 400 |

## 6. Comandos

```bash
mvnw clean test          # Rodar todos os testes
mvnw verify             # Rodar testes + cobertura JaCoCo
```
