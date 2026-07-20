## ADDED Requirements

### Requirement: Gerenciar comandas individuais por cliente
O sistema SHALL permitir criar comandas para cada cliente em uma mesa. Cada comanda contém itens individuais e possui status que segue o ciclo: `ABERTA` → `PAGA` (ou `AGUARDANDO_PIX` via PIX), ou `CANCELADA` se o cliente desistir. Itens compartilhados são gerenciados no pool da mesa (`ItemCompartilhado`), não na comanda.

#### Scenario: Criar comanda para cliente na mesa
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas/{mesaId}/comandas` com `clienteNome` e lista de `itens`
- **THEN** o sistema retorna 201 Created com status `ABERTA`
- **THEN** se a mesa estava `LIVRE`, ela muda automaticamente para `OCUPADA`

#### Scenario: Listar comandas de uma mesa
- **WHEN** um garçom envia GET para `/API/V1/comandas?mesaId={id}`
- **THEN** o sistema retorna 200 OK com as comandas da mesa ordenadas por data

#### Scenario: Adicionar item individual à comanda
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/itens` com `produtoId` e `quantidade`
- **THEN** o sistema retorna 201 Created e o item é adicionado à comanda

#### Scenario: Atualizar item da comanda
- **WHEN** um garçom envia PUT para `/API/V1/comandas/{comandaId}/itens/{itemId}` com `produtoId` e `quantidade`
- **THEN** o sistema retorna 200 OK com o item atualizado

#### Scenario: Remover item da comanda
- **WHEN** um garçom envia DELETE para `/API/V1/comandas/{id}/itens/{itemId}`
- **THEN** o sistema retorna 204 No Content

### Requirement: Rateio de itens compartilhados
O sistema SHALL permitir que um cliente pague parte do valor de um item compartilhado. A validação de saldo consulta o pool de `ItemCompartilhado` da mesa. O rateio é registrado por comanda, produto e valor pago.

#### Scenario: Cliente paga parte de item compartilhado
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/rateio` com `produtoId` e `valorPago`
- **THEN** o sistema valida o saldo contra `mesa.itensCompartilhados` e registra o rateio na comanda

#### Scenario: Rateio excede valor do item
- **WHEN** um garçom tenta registrar rateio com `valorPago` que excede o saldo restante do item no pool da mesa
- **THEN** o sistema retorna 400 Bad Request com mensagem de erro

### Requirement: Fechar comanda individual
O sistema SHALL permitir fechar a comanda de um cliente, marcando como PAGA. A mesa só é liberada quando todas as comandas estiverem PAGA ou CANCELADA.

#### Scenario: Fechar comanda de cliente
- **WHEN** um garçom envia POST para `/API/V1/comandas/{id}/fechar` com `{"formaPagamento": "DINHEIRO|MAQUININHA|PIX"}`
- **THEN** o sistema retorna 200 OK e a comanda muda para status `PAGA` (ou `AGUARDANDO_PIX` se for PIX)

#### Scenario: Fechar última comanda libera mesa e limpa compartilhados
- **WHEN** um garçom fecha a última comanda ABERTA de uma mesa
- **THEN** os itens compartilhados da mesa são removidos automaticamente
- **THEN** a mesa muda automaticamente para status `LIVRE`

#### Scenario: Fechar comanda já paga
- **WHEN** um garçom tenta fechar uma comanda com status `PAGA`
- **THEN** o sistema retorna 400 Bad Request

### Requirement: Cancelar comandas (encerramento de mesa)
O sistema SHALL permitir cancelar todas as comandas abertas de uma mesa de uma vez, preservando todo o histórico de pedidos, itens, valores e rateios no banco de dados.

#### Scenario: Encerrar mesa cancelando comandas abertas
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas/{id}/encerrar`
- **THEN** todas as comandas com status `ABERTA` ou `AGUARDANDO_PIX` mudam para `CANCELADA`
- **THEN** o status da mesa muda para `LIVRE`
- **THEN** itens, valores, datas e vínculos com a mesa são preservados para histórico

#### Scenario: Encerrar mesa sem comandas abertas
- **WHEN** um garçom envia POST para `/API/V1/mesas/{id}/encerrar` e todas as comandas já estão em status final (`PAGA` ou `CANCELADA`)
- **THEN** o sistema retorna 200 OK e a mesa muda para `LIVRE` (sem alterar comandas)

### Requirement: Status de comanda e ciclo de vida
O sistema SHALL gerenciar os status de uma comanda seguindo o ciclo de vida definido.

#### Scenario: Transições válidas de status
- `ABERTA` → `PAGA` (garçom fecha comanda via DINHEIRO/MAQUININHA/CARTAO)
- `ABERTA` → `AGUARDANDO_PIX` (garçom gera QR Code PIX, backend altera status)
- `AGUARDANDO_PIX` → `PAGA` (webhook do Mercado Pago confirma pagamento)
- `ABERTA` → `CANCELADA` (encerramento de mesa)
- `AGUARDANDO_PIX` → `CANCELADA` (encerramento de mesa)

#### Scenario: Status finais (sem reversão)
- `PAGA` — pagamento concluído
- `CANCELADA` — comanda cancelada (histórico preservado)

#### Scenario: Dados preservados após cancelamento
- **WHEN** uma comanda muda para `CANCELADA`
- **THEN** todos os itens, valores e rateios permanecem no banco para fins de histórico
- **THEN** a comanda pode ser consultada em relatórios de desistência
