## Context

Atualmente a API atende apenas delivery: pedidos online com endereço de entrega, taxa de entrega por região e cliente logado ou convidado. O modelo de `Pedido` carrega toda a lógica num único fluxo.

A nova demanda é atender também comanda digital presencial em praças de alimentação, onde:
- Clientes são identificados por nome (sem cadastro)
- Garçons registram pedidos por mesa
- Cada cliente tem sua própria comanda (pode pagar e sair separadamente)
- Itens compartilhados (ex: Coca 2L) são rateados de forma flexível

A abordagem é unificar os dois cenários adicionando um campo `origem` no Pedido e criando entidades específicas para o fluxo presencial.

## Goals / Non-Goals

**Goals:**
- Criar entidades `Mesa`, `Comanda`, `ComandaRateio`
- Adicionar campo `origem` no `Pedido` (DELIVERY | PRESENCIAL)
- Adicionar role `GARCOM` no enum `Role`
- CRUD de mesas vinculadas ao restaurante
- CRUD de comandas por mesa, com itens individuais e compartilhados
- Rateio flexível: cliente paga o valor que quiser de um item compartilhado
- Fechamento individual de comanda
- Delivery existente continua funcionando sem alterações
- Seguir os padrões do projeto (BeanUtils, DTOs, @ControllerAdvice, etc.)

**Non-Goals:**
- Não haverá estoque ou controle de inventário
- Não haverá impressão de comanda (apenas API)
- Não haverá integração com maquininha de cartão
- Não haverá controle de horário de funcionamento para mesas

## Modelo de Dados

```
┌─────────────────────┐       ┌───────────────────────────┐
│     Restaurante      │       │          Mesa             │
├─────────────────────┤1     N│───────────────────────────┤
│ id                  │◄──────│ id                        │
│ nome                │       │ nomeCliente               │ ← nome do cliente principal
│ ...                 │       │ status (LIVRE|OCUPADA)   │
└─────────────────────┘       │ dataAbertura              │
                              │ restaurante_id (FK)       │
                              └──────┬────────────────────┘
                                     │ 1
                                     │ N
                              ┌──────▼────────────────────┐
                              │        Comanda             │
                              ├───────────────────────────┤
                              │ id                        │
                              │ mesa_id (FK)              │
                              │ garcom_id (FK → Usuario)  │
                              │ clienteNome               │ ← nome do cliente
                              │ status (ABERTA|FECHADA|PAGA)
                              │ dataAbertura              │
                              │ dataFechamento            │
                              │ valorTotal                │
                              └──────┬────────────────────┘
                                     │ 1
                                     │ N
                              ┌──────▼──────────────────────────┐
                              │       ComandaItem               │
                              ├─────────────────────────────────┤
                              │ id                              │
                              │ comanda_id (FK)                 │
                              │ produto_id (FK)                 │
                              │ quantidade                      │
                              │ precoUnitario                   │
                              │ compartilhado (boolean)         │ ← true = item da mesa, sem dono
                              └─────────────────────────────────┘

                              ┌─────────────────────────────────┐
                              │      ComandaRateio              │
                              ├─────────────────────────────────┤
                              │ id                              │
                              │ comanda_id (FK)                 │
                              │ produto_id (FK)                 │
                              │ valorPago (BigDecimal)          │
                              │ dataPagamento                   │
                              └─────────────────────────────────┘
```

### Decisão 1: Comanda separada da mesa, não do Pedido
Cada cliente na mesa tem sua própria comanda. Isso permite que cada um pague individualmente e saia quando quiser, mantendo a mesa ocupada para os demais.

### Decisão 2: Item compartilhado sem dono no momento do pedido
O item compartilhado (ex: Coca) é adicionado apenas uma vez ao pedido da mesa, com flag `compartilhado = true`. Não pertence a ninguém até que um cliente, ao pagar, registre um `ComandaRateio` com o valor que está pagando.

### Decisão 3: Rateio é um registro de pagamento, não uma divisão
Diferente de dividir igualmente, o `ComandaRateio` registra "fulano pagou R$ X deste produto". Isso permite rateios assimétricos e pagamentos parciais. O item está totalmente pago quando `SUM(valorPago) >= precoUnitario × quantidade`.

### Decisão 4: Origem no Pedido como enum, não herança
Em vez de criar duas classes (PedidoDelivery, PedidoPresencial), usar um campo `origem` (DELIVERY | PRESENCIAL) com validações condicionais. Delivery exige endereço/taxa; presencial não. Isso mantém a simplicidade.

### Decisão 5: Garçom usa o mesmo sistema de autenticação
`ROLE_GARCOM` entra no enum `Role`. Dono de restaurante pode cadastrar garçons como usuários do sistema com esse papel. Garçom faz login normal e recebe JWT.

## Endpoints

### Mesas

| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/API/V1/mesas` | Abrir mesa |
| GET | `/API/V1/mesas/{id}` | Buscar mesa |
| PUT | `/API/V1/mesas/{id}` | Atualizar mesa (ex: mudar nome) |
| GET | `/API/V1/mesas?restauranteId={id}` | Listar mesas do restaurante |
| DELETE | `/API/V1/mesas/{id}` | Fechar/liberar mesa |

### Comandas

| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/API/V1/mesas/{mesaId}/comandas` | Criar comanda para um cliente |
| GET | `/API/V1/comandas?mesaId={id}` | Listar comandas da mesa |
| GET | `/API/V1/comandas/{id}` | Buscar comanda com detalhes |
| POST | `/API/V1/comandas/{id}/itens` | Adicionar item à comanda |
| DELETE | `/API/V1/comandas/{id}/itens/{itemId}` | Remover item da comanda |
| POST | `/API/V1/comandas/{id}/rateio` | Registrar pagamento de parte de item compartilhado |
| POST | `/API/V1/comandas/{id}/fechar` | Fechar comanda do cliente |

## Fluxos

### Abrir mesa e fazer pedido
```
Garçom → POST /mesas (cria mesa "João")
Garçom → POST /mesas/1/comandas (cria comanda para "João" com itens dele)
Garçom → POST /mesas/1/comandas (cria comanda para "Maria" com itens dela)
Garçom → POST /comandas/1/itens?compartilhado=true (adiciona Coca como item compartilhado à mesa)
```

### Rateio e fechamento
```
João quer pagar → POST /comandas/1/rateio { produtoId: 5, valorPago: 5.00 }
                  → Coca tem R$ 5 pagos por João, R$ 7 pendentes
João quer sair  → POST /comandas/1/fechar → comanda status PAGA
                  → Mesa continua OCUPADA (Maria ainda lá)
Maria paga o resto → POST /comandas/2/rateio { produtoId: 5, valorPago: 7.00 }
                    → Coca totalmente paga
Maria sai        → POST /comandas/2/fechar → comanda PAGA
                  → Sem mais comandas abertas → mesa volta a LIVRE
```

## Segurança

- `ROLE_GARCOM` pode gerenciar mesas e comandas (criar, listar, adicionar itens, rateio)
- `ROLE_DONO_RESTAURANTE` pode ver tudo (dashboard)
- `ROLE_CLIENTE` apenas delivery (sem acesso a mesas/comandas)
- Endpoints de mesa/comanda protegidos por autenticação

## DTOs

### MesaRequestDTO
```java
@NotNull Long restauranteId;
@NotBlank String nomeCliente;
```

### MesaResponseDTO
```java
Long id;
String nomeCliente;
String status; // LIVRE, OCUPADA
LocalDateTime dataAbertura;
```

### ComandaRequestDTO (criar comanda)
```java
@NotBlank String clienteNome;
@Valid List<ItemComandaDTO> itens;
```

### ComandaResponseDTO
```java
Long id;
Long mesaId;
String clienteNome;
String status;
BigDecimal valorTotal;
LocalDateTime dataAbertura;
List<ComandaItemDTO> itens;
List<ComandaRateioDTO> rateios;
```

### RateioRequestDTO
```java
@NotNull Long produtoId;
@NotNull @Positive BigDecimal valorPago;
```

## Risks / Trade-offs

- **[Complexidade]** Unificar delivery e presencial num mesmo Pedido pode deixar a entidade grande. Mitigação: usar `origem` e manter validações separadas por tipo.
- **[Rateio manual]** O valor pago é definido manualmente pelo garçom/cliente, o que pode gerar erros. Mitigação: o sistema pode sugerir rateio igual como conveniência futura.
- **[Concorrência]** Dois garçons podem tentar pagar o mesmo item compartilhado ao mesmo tempo. Mitigação: uso de `@Version` (otimistic locking) no `ComandaRateio`.
- **[Mesa órfã]** Garçom abre mesa e vai embora. Mitigação: dono do restaurante pode forçar fechamento de mesa pelo dashboard.

## Open Questions

- Deve-se validar que o garçom pertence ao restaurante da mesa? (Sim, futuramente — por enquanto, qualquer garçom autenticado pode operar)
- Itens compartilhados aparecem no pedido da mesa ou apenas numa comanda especial "da mesa"? (Decisão: aparecem como item com `compartilhado=true` associado a uma comanda fictícia da mesa ou como entidade separada? A implementação pode resolver)
