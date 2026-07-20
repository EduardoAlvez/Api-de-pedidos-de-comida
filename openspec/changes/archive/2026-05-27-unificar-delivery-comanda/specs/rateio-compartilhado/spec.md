## ADDED Requirements

### Requirement: Pedido com origem PRESENCIAL
O sistema SHALL modificar a entidade `Pedido` para incluir um campo `origem` (DELIVERY | PRESENCIAL). Pedidos com origem PRESENCIAL não utilizam taxa de entrega, endereço nem região de entrega.

#### Scenario: Criar pedido presencial
- **WHEN** um garçom cria uma comanda para um cliente
- **THEN** o sistema cria um Pedido com `origem: PRESENCIAL`, `taxaEntrega: 0`

#### Scenario: Pedido delivery continua funcionando
- **WHEN** um cliente faz um pedido online
- **THEN** o sistema cria um Pedido com `origem: DELIVERY`, mantendo toda a lógica de frete e endereço
