## Context

Atualmente a taxa de entrega é um valor fixo de R$ 5,00 definido diretamente no `PedidoService.criar()` (linha 89). O `Restaurante` não possui nenhuma informação sobre regiões de atendimento ou valores de frete. O `PedidoRequestDTO` também não possui campo para especificar a região de entrega.

A abordagem escolhida foi a **lista de locais com frete fixo por região**: cada restaurante cadastra as regiões que atende com um valor de frete específico. No momento do pedido, o cliente informa qual região (ou o sistema infere pelo endereço) e o frete é calculado automaticamente.

## Goals / Non-Goals

**Goals:**
- Criar entidade `RegiaoEntrega` vinculada ao `Restaurante` com nome e valor do frete
- CRUD completo de regiões de entrega via API
- Calcular o frete automaticamente ao criar um pedido com base na região informada
- Remover o valor fixo de R$ 5,00
- Atualizar o `PedidoRequestDTO` para aceitar o ID da região de entrega
- Retornar a taxa de entrega calculada no `PedidoResponseDTO`

**Non-Goals:**
- Não será implementado cálculo por CEP/distância (pode ser futura evolução)
- Não haverá validação de horário de funcionamento para entrega
- Não haverá geolocalização ou mapas

## Decisions

### Decisão 1: Nova entidade `RegiaoEntrega` em vez de campo textual no Restaurante
Criar uma entidade separada permite que um restaurante tenha múltiplas regiões, cada uma com seu próprio valor de frete. É mais flexível que armazenar um JSON ou campos repetidos.

### Decisão 2: O cliente informa `regiaoEntregaId` no request do pedido
Em vez de tentar inferir a região pelo endereço (que exigiria validação complexa), o cliente informa diretamente qual região deseja. O sistema valida se a região pertence ao restaurante do pedido. A validação de que o endereço informado condiz com a região fica para o restaurante no momento da confirmação.

### Decisão 3: CRUD de regiões aninhado ao restaurante
As rotas seguem o padrão `/API/V1/restaurantes/{restauranteId}/regioes`, deixando explícito que regiões pertencem a um restaurante. A autenticação e a verificação de dono do restaurante são herdadas do contexto.

### Decisão 4: Seguir o padrão existente do projeto (BeanUtils, DTOs aninhados)
Manter consistência com o resto do código usando `BeanUtils.copyProperties`, DTOs aninhados, `@ControllerAdvice` para erros, e validação com Bean Validation.

## Modelo de Dados

```
┌─────────────────────┐       ┌──────────────────────────────┐
│     Restaurante      │       │       RegiaoEntrega          │
├─────────────────────┤       ├──────────────────────────────┤
│ id (PK)             │1     N│ id (PK)                      │
│ nome                │◄──────│ nome (varchar, ex: "Centro") │
│ endereco            │       │ valorFrete (BigDecimal)      │
│ ...                 │       │ restaurante_id (FK)          │
└─────────────────────┘       └──────────────────────────────┘
                                       │
                                       │ 1
                                       │
                              ┌────────▼─────────┐
                              │      Pedido       │
                              ├──────────────────┤
                              │ regiaoEntregaId  │ (FK, nullable)
                              │ taxaEntrega       │ ← calculado
                              │ ...               │
                              └───────────────────┘
```

## Fluxo de Criação do Pedido

```
1. Cliente → POST /API/V1/pedidos
   Body: { ..., "regiaoEntregaId": 5, "restauranteId": 1, ... }

2. Sistema busca Restaurante + valida existência

3. Sistema busca RegiaoEntrega por ID
   ├── Se não encontrada → 404
   ├── Se não pertence ao restaurante do pedido → 422
   └── Se OK → obtém valorFrete

4. Subtotal = Σ (precoUnitario × quantidade)
   taxaEntrega = regiaoEntrega.getValorFrete()
   valorTotal = subtotal + taxaEntrega

5. Persiste Pedido + Pagamento + retorna Response
```

## Novos Endpoints

### CRUD de Regiões de Entrega

| Método | Path | Autenticação | Descrição |
|--------|------|-------------|-----------|
| GET | `/API/V1/restaurantes/{restauranteId}/regioes` | Autenticado | Listar regiões do restaurante |
| GET | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Autenticado | Buscar região por ID |
| POST | `/API/V1/restaurantes/{restauranteId}/regioes` | Autenticado | Criar região |
| PUT | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Autenticado | Atualizar região |
| DELETE | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Autenticado | Remover região |

### Alteração em Endpoint Existente

| Método | Path | Mudança |
|--------|------|---------|
| POST | `/API/V1/pedidos` | Agora aceita `regiaoEntregaId` e calcula frete dinamicamente |

## Validações

- `regiaoEntregaId` deve existir no banco
- `regiaoEntregaId` deve pertencer ao `restauranteId` do pedido
- `valorFrete` deve ser positivo (≥ 0)
- `nome` da região não pode ser vazio (min 2 caracteres)

## DTOs

### RegiaoEntregaRequestDTO
```java
@NotBlank String nome;
@NotNull @PositiveOrZero BigDecimal valorFrete;
```

### RegiaoEntregaResponseDTO
```java
Long id;
String nome;
BigDecimal valorFrete;
```

### PedidoRequestDTO (alterado)
```java
// + novo campo
private Long regiaoEntregaId; // opcional por enquanto, se null usa frete 0 ou fallback
```

## Risks / Trade-offs

- **[Simplicidade vs Precisão]** A abordagem por regiões pré-cadastradas é menos precisa que cálculo por CEP, mas é mais simples de implementar e gerenciar. O dono do restaurante define manualmente as regiões.
- **[Manutenção]** Se um restaurante tiver muitas regiões, o cadastro pode ser tedioso. Mitigação: o CRUD permite fácil gerenciamento.
- **[Cliente informa região]** O cliente pode selecionar a região errada. Mas o restaurante pode ajustar na confirmação do pedido (futuro).

## Open Questions

- Deve-se permitir frete grátis (valor 0)?
  - **Resposta**: Sim, `@PositiveOrZero` permite zero.
- Deve haver uma região "padrão" ou fallback se `regiaoEntregaId` não for informado?
  - **Resposta**: Se não informado, manter taxa 0 ou lançar erro. Definir em implementação.
- Dono do restaurante pode gerenciar regiões de qualquer restaurante ou só do seu?
  - **Resposta**: Futuramente validar por autenticação. Por ora, qualquer usuário autenticado pode gerenciar.
