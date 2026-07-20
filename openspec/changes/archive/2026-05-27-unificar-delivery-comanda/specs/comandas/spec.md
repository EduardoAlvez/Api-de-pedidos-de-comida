## ADDED Requirements

### Requirement: Gerenciar comandas individuais por cliente
O sistema SHALL permitir criar comandas para cada cliente em uma mesa. Cada comanda contém itens (individuais ou compartilhados) e possui status (ABERTA, FECHADA, PAGA).

#### Scenario: Criar comanda para cliente na mesa
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas/{mesaId}/comandas` com `clienteNome` e lista de `itens`
- **THEN** o sistema retorna 201 Created com status `ABERTA`

#### Scenario: Listar comandas de uma mesa
- **WHEN** um garçom envia GET para `/API/V1/comandas?mesaId={id}`
- **THEN** o sistema retorna 200 OK com as comandas da mesa ordenadas por data

#### Scenario: Adicionar item individual à comanda
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/itens` com `produtoId` e `quantidade`
- **THEN** o sistema retorna 200 OK e o item é adicionado à comanda

#### Scenario: Adicionar item compartilhado (sem dono)
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/itens` com `compartilhado: true`
- **THEN** o item é marcado como compartilhado e não pertence a nenhum cliente específico

#### Scenario: Remover item da comanda
- **WHEN** um garçom envia DELETE para `/API/V1/comandas/{id}/itens/{itemId}`
- **THEN** o sistema retorna 204 No Content

### Requirement: Rateio de itens compartilhados
O sistema SHALL permitir que um cliente pague parte do valor de um item compartilhado. O rateio é registrado por comanda, produto e valor pago.

#### Scenario: Cliente paga parte de item compartilhado
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/rateio` com `produtoId` e `valorPago`
- **THEN** o sistema cria um rateio e reduz o saldo pendente do item

#### Scenario: Rateio excede valor do item
- **WHEN** um garçom tenta registrar rateio com `valorPago` que excede o saldo restante do item
- **THEN** o sistema retorna 400 Bad Request com mensagem de erro

### Requirement: Fechar comanda individual
O sistema SHALL permitir fechar a comanda de um cliente, marcando como PAGA. A mesa só é liberada quando todas as comandas estiverem PAGA.

#### Scenario: Fechar comanda de cliente
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/fechar`
- **THEN** a comanda muda para status `PAGA`

#### Scenario: Fechar última comanda libera mesa
- **WHEN** um garçom fecha a última comanda ABERTA/FECHADA de uma mesa
- **THEN** a mesa muda automaticamente para status `LIVRE`

#### Scenario: Fechar comanda já paga
- **WHEN** um garçom tenta fechar uma comanda com status `PAGA`
- **THEN** o sistema retorna 400 Bad Request
