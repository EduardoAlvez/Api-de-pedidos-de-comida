## Context

A plataforma de pedidos atende multiplos restaurantes, mas o modelo de dados atual nao separa o acesso por estabelecimento. Um usuario com role GARCOM ou DONO_RESTAURANTE consegue acessar livremente dados de qualquer restaurante. Isso acontece porque:

1. `Usuario` nao possui vinculo com restaurante para o role GARCOM (apenas DONO_RESTAURANTE tem `@OneToOne Restaurante` via `Restaurante.usuario_id`)
2. Nenhuma query nos repositories ou services filtra por restaurante
3. O `SecurityFilter` valida apenas se o JWT eh valido, sem verificar permissoes de acesso a dados

## Goals / Non-Goals

**Goals:**
- GARCOM e DONO_RESTAURANTE acessam apenas dados do restaurante ao qual estao vinculados
- CLIENTE continua podendo ver todos os restaurantes e fazer pedidos em qualquer um
- DONO pode vincular e desvincular garcons ao seu restaurante
- Garcom sem restaurante vinculado nao consegue acessar dados que dependem de restaurante
- Metodo `getRestauranteVinculado()` em Usuario abstrai a logica de qual field usar conforme o role

**Non-Goals:**
- Nao implementar paginacao (esta fora do escopo)
- Nao alterar o fluxo de autenticacao/login
- Nao adicionar controle de acesso por permissao fina (ex: "garcom pode so ler, dono pode escrever") — apenas isolamento por restaurante

## Decisions

### 1. Novo campo `restauranteTrabalho` em Usuario (@ManyToOne)

| Alternativa | Decisao |
|-------------|---------|
| **Escolhida**: Novo campo `@ManyToOne Restaurante restauranteTrabalho` em Usuario | Nao quebra o relacionamento existente `@OneToOne Restaurante restaurante` (do DONO) |
| Separar tabela `UsuarioRestaurante` (join table) | Superengenharia para um vinculo simples 1/N |
| Unificar em um unico campo `restaurante` no Usuario e remover `usuario_id` do Restaurante | Mudanca breaking maior, exige migracao de dados dos DONOs existentes |

### 2. `getRestauranteVinculado()` como metodo de instancia em Usuario

Centraliza a logica: se DONO_RESTAURANTE retorna `this.restaurante` (OneToOne existente); se GARCOM retorna `this.restauranteTrabalho` (ManyToOne novo); se CLIENTE retorna null. Evita duplicar if/else em cada service.

### 3. Extracao do usuario logado via SecurityContextHolder

Criar classe utilitaria `SecurityUtils` com metodo estatico `getUsuarioLogado()`. Isso evita injetar o usuario em cada metodo de controller manualmente.

Alternativa rejeitada: passar `@AuthenticationPrincipal Usuario usuario` nos controllers — funciona, mas obriga toda controller a declarar o parametro. A abordagem com SecurityUtils eh mais DRY e segura (ninguem "esquece" de passar).

### 4. Novo endpoint `POST /API/V1/restaurantes/{id}/garcons`

Apenas DONO_RESTAURANTE pode chamar. Valida que o usuario logado eh dono do restaurante com o ID fornecido. Recebe `usuarioId` no body e valida que o usuario alvo tem role GARCOM.

### 5. Filtro por restaurante nos repositories

Criar metodos como `findByRestauranteId(Long restauranteId)` nos repositories existentes. Os services chamam estes metodos em vez de `findAll()`.

### 6. Validacao de acesso em operacoes por ID

Para endpoints GET/PUT/DELETE de recursos que pertencem a um restaurante (mesa, comanda, produto, pedido), o service deve verificar se o recurso pertence ao restaurante do usuario logado. Caso contrario, retornar 404 (nao informar se o recurso existe ou nao — seguranca).

## Risks / Trade-offs

| Risco | Mitigacao |
|-------|-----------|
| CLIENTE chamar endpoint de garcom/dono e receber erro 403 inesperado | A role GARCOM/DONO nao muda — CLIENTE ja nao deveria acessar esses endpoints. Nao ha mudanca nas permissoes existentes. |
| Performance: queries com filtro extra de restaurante | Adicionar indices na coluna `restaurante_id` nas tabelas relevantes (Mesa, Comanda, Pedido, Produto) |
| Garcom ser vinculado a restaurante que nao existe | Validar existencia do restaurante no momento do vinculo |
| Mudanca breaking para clientes existentes (se algum cliente dependia de listar dados sem filtro) | Endpoints atuais sao usados pelo front-end web; coordenar deploy. Documentado no proposal como BREAKING. |
| Usuario com role GARCOM mas sem `restauranteTrabalho` vinculado tentar operacoes | Retornar 400 Bad Request com mensagem clara "Usuario nao vinculado a nenhum restaurante" |

## Diagrama de Entidades

```
┌─────────────────────────────────────────────────────────┐
│                      Usuario                            │
├─────────────────────────────────────────────────────────┤
│ id: Long                                                │
│ nome: String                                            │
│ tipo: Role (DONO_RESTAURANTE | GARCOM | CLIENTE)        │
│ restTrabalho_id: Long (FK → Restaurante.id) ← NOVO     │
│                                                         │
│ getRestauranteVinculado():                              │
│   if DONO → this.restaurante (OneToOne existente)       │
│   if GARCOM → this.restauranteTrabalho (ManyToOne novo) │
│   if CLIENTE → null                                     │
└────────┬────────────────────────────────────────────────┘
         │
         │ @ManyToOne (GARCOM)
         │ @OneToOne (DONO, via Restaurante.usuario_id)
         ▼
┌─────────────────────────────────────────────────────────┐
│                     Restaurante                         │
├─────────────────────────────────────────────────────────┤
│ id: Long                                                │
│ usuario_id: Long (FK → Usuario.id) — DONO existente     │
│                                                         │
│ garcons: List<Usuario> (@OneToMany) ← NOVO              │
└─────────────────────────────────────────────────────────┘
```

## Estrategia de Implementacao

1. Adicionar campos nas entidades (`usuario.restauranteTrabalho`, `restaurante.garcons`)
2. Criar `SecurityUtils.getUsuarioLogado()` e `Usuario.getRestauranteVinculado()`
3. Atualizar repositories com metodos `findByRestauranteId()`
4. Atualizar services para receber usuario logado e filtrar
5. Atualizar controllers para extrair usuario logado
6. Adicionar endpoint de vinculo de garcons pelo DONO
7. Adicionar indices no banco
8. Testar cada cenario BDD do proposal
