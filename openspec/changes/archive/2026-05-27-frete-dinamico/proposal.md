## Why

Atualmente a taxa de entrega é fixa em R$ 5,00 para todos os pedidos, independente do restaurante ou da localização do cliente. Isso não reflete a realidade: cada restaurante tem regiões de atendimento diferentes e custos de entrega distintos. A proposta é tornar o frete dinâmico, calculado automaticamente no momento da criação do pedido.

## What Changes

- Substituir a taxa de entrega fixa (R$ 5,00) por um cálculo dinâmico
- Cada restaurante poderá cadastrar regiões/localidades que atende, cada uma com seu próprio valor de frete
- Durante a criação do pedido, o sistema identificará a região de entrega e aplicará o frete correspondente
- O valor do frete será exibido no response do pedido e incluído no valor total
- Novos endpoints para CRUD das regiões de atendimento de cada restaurante
- O PedidoRequestDTO passará a receber um identificador da região de entrega (ou o endereço será usado para determinar a região)

## Capabilities

### New Capabilities
- `regioes-entrega`: Gerenciamento das regiões que cada restaurante atende, com valor de frete por região

### Modified Capabilities
<!-- Nenhuma spec existente será alterada em nível de requisitos -->

## Impact

**Entidades afetadas:**
- `Restaurante`: receberá um relacionamento `@OneToMany` para as regiões de atendimento
- `Pedido`: a lógica de `taxaEntrega` deixará de ser hardcoded

**Novas entidades:**
- `RegiaoEntrega`: id, nome, valorFrete (BigDecimal), restaurante (ManyToOne)

**APIs novas:**
- `GET /API/V1/restaurantes/{id}/regioes` — listar regiões de um restaurante
- `POST /API/V1/restaurantes/{id}/regioes` — adicionar região
- `PUT /API/V1/restaurantes/{id}/regioes/{regiaoId}` — atualizar região
- `DELETE /API/V1/restaurantes/{id}/regioes/{regiaoId}` — remover região

**API alterada:**
- `POST /API/V1/pedidos` — o cálculo do frete será automático com base na região informada

**BD:**
- Nova tabela `regiao_entrega`

**Cenário BDD — Dono de restaurante cadastra regiões de entrega**

```gherkin
Funcionalidade: Cadastro de regiões de entrega
  Como um dono de restaurante
  Quero definir as regiões que meu restaurante atende e o valor do frete para cada uma
  Para que o frete seja calculado automaticamente nos pedidos

  Cenário: Dono cadastra uma região de entrega com sucesso
    Dado que existe um restaurante cadastrado com ID 1
    E o usuário autenticado é dono desse restaurante
    Quando eu enviar uma requisição POST para "/API/V1/restaurantes/1/regioes"
    E o corpo da requisição contiver:
      | campo      | valor        |
      | nome       | "Centro"     |
      | valorFrete | 8.50         |
    Então o sistema deve retornar status 201 Created
    E o response deve conter os dados da região cadastrada com ID gerado
    E a região "Centro" deve estar associada ao restaurante 1 no banco de dados
```

**Cenário BDD — Pedido calcula frete automaticamente**

```gherkin
Funcionalidade: Cálculo do frete no pedido
  Como um cliente
  Quero que o frete seja calculado automaticamente com base na região de entrega
  Para saber o valor total do pedido antes de finalizar

  Cenário: Cliente faz pedido informando a região de entrega
    Dado que o restaurante 1 atende a região "Centro" com frete de R$ 8,50
    E o cliente está autenticado
    Quando eu enviar uma requisição POST para "/API/V1/pedidos"
    E o corpo da requisição contiver "regiaoEntregaId" igual ao ID da região "Centro"
    Então o sistema deve calcular o subtotal dos itens
    E a taxa de entrega deve ser R$ 8,50 (valor da região)
    E o valor total deve ser subtotal + R$ 8,50
    E o response deve conter "taxaEntrega": 8.50
```

**Cenário BDD — Região inválida no pedido**

```gherkin
Cenario: Cliente tenta usar uma região que não pertence ao restaurante
    Dado que o restaurante 1 atende apenas a região "Centro"
    E o cliente informa o ID de uma região que não pertence ao restaurante 1
    Quando eu enviar uma requisição POST para "/API/V1/pedidos"
    Então o sistema deve retornar status 422 Unprocessable Entity
    E a mensagem de erro deve informar que a região não atende este restaurante
```
