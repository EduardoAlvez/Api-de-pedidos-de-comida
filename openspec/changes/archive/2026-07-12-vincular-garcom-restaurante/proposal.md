## Why

Atualmente qualquer usuario autenticado com role GARCOM ou DONO_RESTAURANTE consegue acessar dados de qualquer restaurante da plataforma — listar produtos, gerenciar mesas, abrir comandas e visualizar pedidos de estabelecimentos concorrentes. Isso viola o principio de隔离amento de dados (data isolation) entre restaurantes e impossibilita o uso seguro da plataforma por multiplos estabelecimentos. A mudanca garante que cada GARCOM e DONO_RESTAURANTE enxergue apenas os dados do restaurante ao qual esta vinculado.

## What Changes

- Adicionar coluna `restaurante_id` (FK) na entidade `Usuario` para vincular GARCOM a um restaurante
- Adicionar relacionamento `@OneToMany` de `Restaurante` para `Usuario` (lista de garcons)
- Criar metodo `getRestauranteVinculado()` em `Usuario` que retorna o restaurante conforme o tipo (DONO via `@OneToOne` existente, GARCOM via novo `@ManyToOne`)
- Extrair usuario logado do SecurityContextHolder em todos os controllers/services que acessam dados por restaurante
- Filtrar todas as queries de produtos, mesas, comandas, pedidos e regioes de entrega pelo restaurante do usuario logado
- Adicionar endpoint `POST /API/V1/restaurantes/{id}/garcons` para DONO vincular garcons ao seu restaurante
- Adicionar validacoes de autorizacao: GARCOM/DONO nao podem acessar dados de restaurantes que nao sejam os seus
- **BREAKING**: metodos de listagem (GET /mesas, GET /comandas, GET /pedidos/usuario/{id}) passam a filtrar por restaurante; clientes que esperavam ver dados de multiplos restaurantes precisarao se adaptar

## Capabilities

### New Capabilities
- `vincular-garcons`: vincular e desvincular usuarios com role GARCOM a um restaurante
- `isolamento-dados-restaurante`: garantir que cada usuario veja apenas dados do seu restaurante

### Modified Capabilities
- Nenhuma — todos os requisitos de negocio existentes permanecem, apenas o comportamento de acesso a dados muda

## Impact

- **Modelos**: `Usuario.java` ganha campo `restauranteTrabalho` (`@ManyToOne`); `Restaurante.java` ganha campo `garcons` (`@OneToMany`)
- **Controllers**: `MesaController`, `ComandaController`, `PedidoController`, `ProdutoController`, `RegiaoEntregaController` — injecao do usuario logado e filtro por restaurante
- **Services**: metodos existentes recebem parametro `Usuario` ou `Long restauranteId`; novas validacoes de permissao
- **Repositories**: novos metodos `findByRestauranteId()` onde necessario
- **Seguranca**: `SecurityFilter` ja extrai o usuario — reutilizar via `SecurityContextHolder`
- **API**: novo endpoint `POST/DELETE /API/V1/restaurantes/{id}/garcons`

## Cenario BDD

```gherkin
Cenario: Garcom listar apenas produtos do seu restaurante
  Dado que existe um garcom "Carlos" vinculado ao restaurante "PizzaBus"
  E que existem produtos cadastrados no "PizzaBus" e no "SushiGo"
  Quando o garcom "Carlos" faz GET /API/V1/produtos/restaurante/1 (sendo 1 o ID do PizzaBus)
  Entao a resposta contem apenas os produtos do "PizzaBus"
  E nao contem nenhum produto do "SushiGo"

Cenario: Garcom tentar acessar comanda de outro restaurante
  Dado que existe um garcom "Ana" vinculado ao restaurante "PizzaBus"
  E que existe uma comanda aberta no "SushiGo" com ID 99
  Quando o garcom "Ana" faz GET /API/V1/comandas/99
  Entao a resposta deve ser 404 Not Found

Cenario: Dono vincular garcom ao seu restaurante
  Dado que existe um dono "Joao" dono do restaurante "PizzaBus"
  E que existe um usuario "Carlos" com role GARCOM
  Quando o dono "Joao" faz POST /API/V1/restaurantes/1/garcons com body {"usuarioId": 2}
  Entao o garcom "Carlos" passa a estar vinculado ao "PizzaBus"

Cenario: Garcom nao vinculado tentar listar comandas
  Dado que existe um usuario "Pedro" com role GARCOM sem restaurante vinculado
  Quando o garcom "Pedro" faz GET /API/V1/comandas
  Entao a resposta deve ser 400 Bad Request com mensagem "Usuario nao vinculado a nenhum restaurante"
```
