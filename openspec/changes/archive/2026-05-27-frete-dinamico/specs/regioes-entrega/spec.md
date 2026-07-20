## ADDED Requirements

### Requirement: Dono do restaurante gerencia regiões de entrega
O sistema SHALL permitir que donos de restaurante cadastrem, listem, atualizem e removam regiões de entrega. Cada região possui um nome e um valor de frete. As regiões são vinculadas a um restaurante específico.

#### Scenario: Criar região de entrega com sucesso
- **WHEN** um usuário autenticado envia POST para `/API/V1/restaurantes/{restauranteId}/regioes` com `nome` e `valorFrete` válidos
- **THEN** o sistema retorna 201 Created com os dados da região criada

#### Scenario: Criar região sem autenticação
- **WHEN** um usuário não autenticado envia POST para `/API/V1/restaurantes/{restauranteId}/regioes`
- **THEN** o sistema retorna 401 Unauthorized

#### Scenario: Criar região com nome vazio
- **WHEN** um usuário envia POST com `nome` em branco
- **THEN** o sistema retorna 422 Unprocessable Entity com erro de validação no campo `nome`

#### Scenario: Criar região com valor de frete negativo
- **WHEN** um usuário envia POST com `valorFrete` negativo
- **THEN** o sistema retorna 422 Unprocessable Entity com erro de validação no campo `valorFrete`

#### Scenario: Listar regiões de um restaurante
- **WHEN** um usuário autenticado envia GET para `/API/V1/restaurantes/{restauranteId}/regioes`
- **THEN** o sistema retorna 200 OK com uma lista de regiões (pode ser vazia)

#### Scenario: Atualizar região de entrega
- **WHEN** um usuário autenticado envia PUT para `/API/V1/restaurantes/{restauranteId}/regioes/{id}` com novos dados
- **THEN** o sistema retorna 200 OK com os dados atualizados

#### Scenario: Remover região de entrega
- **WHEN** um usuário autenticado envia DELETE para `/API/V1/restaurantes/{restauranteId}/regioes/{id}`
- **THEN** o sistema retorna 204 No Content

#### Scenario: Remover região inexistente
- **WHEN** um usuário envia DELETE para `/API/V1/restaurantes/{restauranteId}/regioes/{id}` e o ID não existe
- **THEN** o sistema retorna 404 Not Found

### Requirement: Pedido calcula frete automaticamente com base na região
O sistema SHALL calcular a taxa de entrega no momento da criação do pedido usando o valor do frete da região selecionada. O `PedidoRequestDTO` SHALL aceitar um campo `regiaoEntregaId`.

#### Scenario: Criar pedido com região de entrega válida
- **WHEN** um cliente autenticado envia POST para `/API/V1/pedidos` com `regiaoEntregaId` válido e pertencente ao restaurante do pedido
- **THEN** o sistema calcula `taxaEntrega` = valorFrete da região
- **AND** `valorTotal` = `subtotal` + `taxaEntrega`
- **AND** o response contém `taxaEntrega` com o valor calculado

#### Scenario: Criar pedido com região que não pertence ao restaurante
- **WHEN** um cliente envia POST para `/API/V1/pedidos` com `regiaoEntregaId` de uma região que não pertence ao restaurante informado
- **THEN** o sistema retorna 422 Unprocessable Entity com mensagem "A região de entrega não pertence a este restaurante"

#### Scenario: Criar pedido com região inexistente
- **WHEN** um cliente envia POST para `/API/V1/pedidos` com `regiaoEntregaId` que não existe no banco
- **THEN** o sistema retorna 404 Not Found

#### Scenario: Criar pedido sem região de entrega
- **WHEN** um cliente envia POST para `/API/V1/pedidos` sem `regiaoEntregaId`
- **THEN** o sistema calcula `taxaEntrega` = 0 (sem frete) ou retorna erro — a definir em implementação
