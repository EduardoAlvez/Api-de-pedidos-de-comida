## Why

A API atualmente atende apenas delivery online (cliente logado ou convidado, com endereço e frete). Para o TCC, o objetivo é também atender comanda digital presencial em restaurantes, permitindo que garçons registrem pedidos de clientes na mesa, com suporte a itens compartilhados e rateio flexível. A proposta é unificar os dois cenários em uma única API.

## What Changes

- Adiciona campo `origem` no Pedido (`DELIVERY` | `PRESENCIAL`)
- Nova entidade `Mesa`: identificada pelo nome do cliente principal, vinculada ao restaurante
- Nova entidade `Comanda`: representa o pedido individual de cada cliente na mesa
- Nova entidade `ComandaRateio`: registra quanto cada cliente pagou de um item compartilhado
- Novo papel `ROLE_GARCOM`
- Itens compartilhados sem dono fixo — o rateio é definido no momento do pagamento
- Cliente da comanda é identificado apenas por nome, sem login
- Fechamento individual por comanda (cada cliente paga o seu e sai)
- Delivery mantém toda a lógica atual (frete por região, endereço, cliente logado ou convidado)
- **BREAKING**: Pedido ganha campo `origem`; endpoints de pedido precisam tratar os dois fluxos

## Capabilities

### New Capabilities
- `mesas`: Gerenciamento de mesas (abrir, ocupar, liberar)
- `comandas`: Gerenciamento de comandas individuais por cliente na mesa
- `rateio-compartilhado`: Rateio flexível de itens compartilhados entre clientes
- `autenticacao-garcom`: Role GARCOM com permissões específicas

### Modified Capabilities
<!-- Nenhuma spec existente será alterada — são capacidades totalmente novas -->

## Impact

**Entidades novas:**
- `Mesa`: id, nomeCliente, status (LIVRE, OCUPADA), restaurante (ManyToOne)
- `Comanda`: id, mesa (ManyToOne), garcom (ManyToOne), status (ABERTA, FECHADA, PAGA), clienteNome, dataAbertura, valorTotal
- `ComandaRateio`: id, comanda, produto, valorPago, pago (boolean)

**Entidades modificadas:**
- `Pedido`: adiciona campo `origem` (DELIVERY | PRESENCIAL)

**Roles:**
- Adicionar `GARCOM` no enum `Role`
- Garçom tem `ROLE_GARCOM` + `ROLE_CLIENTE`

**Endpoints novos:**
- `POST /API/V1/mesas` — abrir mesa
- `GET /API/V1/mesas/{id}` — status da mesa
- `PUT /API/V1/mesas/{id}` — atualizar mesa
- `GET /API/V1/mesas?restauranteId={id}` — listar mesas
- `POST /API/V1/mesas/{id}/comandas` — criar comanda para um cliente
- `GET /API/V1/comandas?mesaId={id}` — listar comandas da mesa
- `POST /API/V1/comandas/{id}/rateio` — registrar pagamento de parte de item compartilhado
- `POST /API/V1/comandas/{id}/fechar` — fechar comanda do cliente

**Cenário BDD — Garçom abre mesa e registra pedido**

```gherkin
Funcionalidade: Comanda digital presencial
  Como um garçom
  Quero abrir uma mesa e registrar os pedidos dos clientes
  Para que a comanda digital funcione na praça de alimentação

  Cenário: Garçom abre uma mesa com o nome do cliente principal
    Dado que existe um restaurante cadastrado com ID 1
    E um garçom autenticado com papel GARCOM
    Quando eu enviar uma requisição POST para "/API/V1/mesas"
    E o corpo da requisição contiver:
      | campo         | valor        |
      | restauranteId | 1            |
      | nomeCliente   | "João"       |
    Então o sistema deve retornar status 201 Created
    E a mesa deve ter status "LIVRE"

  Cenário: Garçom cria comanda para um cliente na mesa
    Dado que existe uma mesa aberta com ID 1 e nome "João"
    Quando eu enviar POST para "/API/V1/mesas/1/comandas"
    E o corpo contiver:
      | campo       | valor             |
      | clienteNome | "João"            |
      | itens       | [{"produtoId":1, "quantidade":1}] |
    Então o sistema deve retornar status 201 Created
    E a comanda deve ter status "ABERTA"
```

**Cenário BDD — Rateio de item compartilhado**

```gherkin
Funcionalidade: Rateio de itens compartilhados
  Como um garçom
  Quero registrar quanto cada cliente pagou de um item compartilhado
  Para que os clientes possam dividir a conta de forma flexível

  Cenário: Cliente paga parte de um item compartilhado
    Dado que existe uma comanda aberta para "João" na mesa 1
    E existe um item compartilhado "Coca 2L" no valor de R$ 12,00 no pedido da mesa
    Quando o garçom enviar POST para "/API/V1/comandas/1/rateio"
    E o corpo contiver:
      | campo     | valor |
      | produtoId | 5     |
      | valorPago | 5.00  |
    Então o sistema deve criar um registro de rateio
    E o item "Coca 2L" deve ter R$ 5,00 pagos e R$ 7,00 pendentes

  Cenário: Cliente fecha a comanda
    Dado que a comanda de "João" está aberta
    E ele já pagou R$ 5,00 da Coca e R$ 15,00 da batata
    Quando o garçom enviar POST para "/API/V1/comandas/1/fechar"
    Então a comanda deve ter status "PAGA"
    E a mesa deve permanecer OCUPADA se houver outras comandas abertas
```
