## ADDED Requirements

### Requirement: Gerenciar mesas do restaurante
O sistema SHALL permitir que garçons e donos de restaurante gerenciem mesas. Cada mesa pertence a um restaurante, é identificada pelo nome do cliente principal, possui um status (LIVRE ou OCUPADA) e um pool de itens compartilhados (`itensCompartilhados`).

#### Scenario: Garçom abre uma mesa
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas` com `restauranteId` e `nomeCliente` válidos
- **THEN** o sistema retorna 201 Created com status `LIVRE` e `itensCompartilhados` vazio

#### Scenario: Garçom lista mesas de um restaurante
- **WHEN** um garçom autenticado envia GET para `/API/V1/mesas?restauranteId={id}`
- **THEN** o sistema retorna 200 OK com a lista de mesas

#### Scenario: Buscar mesa por ID
- **WHEN** um usuário autenticado envia GET para `/API/V1/mesas/{id}`
- **THEN** o sistema retorna 200 OK com os dados da mesa, incluindo `itensCompartilhados`

#### Scenario: Mesa não encontrada
- **WHEN** um usuário envia GET para `/API/V1/mesas/{id}` e o ID não existe
- **THEN** o sistema retorna 404 Not Found

#### Scenario: Encerrar mesa com comandas abertas (cliente desistiu)
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas/{id}/encerrar`
- **THEN** o sistema cancela todas as comandas com status `ABERTA` ou `AGUARDANDO_PIX` para `CANCELADA`
- **THEN** os itens compartilhados da mesa são removidos
- **THEN** o status da mesa muda para `LIVRE`
- **THEN** o histórico de comandas, itens individuais, valores e rateios é preservado

#### Scenario: Encerrar mesa sem comandas abertas
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas/{id}/encerrar` e não existem comandas abertas
- **THEN** o sistema retorna 200 OK e a mesa muda para `LIVRE`

#### Scenario: Deletar mesa sem comandas abertas
- **WHEN** um garçom autenticado envia DELETE para `/API/V1/mesas/{id}` e a mesa não tem comandas com status `ABERTA` ou `AGUARDANDO_PIX`
- **THEN** o sistema retorna 204 No Content e a mesa é removida permanentemente

#### Scenario: Deletar mesa com comandas abertas (bloqueado)
- **WHEN** um garçom tenta deletar uma mesa que possui comandas com status `ABERTA` ou `AGUARDANDO_PIX`
- **THEN** o sistema retorna 400 Bad Request com mensagem "Não é possível remover a mesa: existem comandas em aberto."

#### Scenario: Deletar mesa com comandas canceladas (permitido)
- **WHEN** um garçom tenta deletar uma mesa que possui apenas comandas com status `CANCELADA`
- **THEN** o sistema permite a exclusão (comandas canceladas não bloqueiam)

### Requirement: Transição automática de status da mesa
O sistema SHALL alterar automaticamente o status da mesa conforme as operações de comanda.

#### Scenario: Mesa fica OCUPADA ao criar comanda
- **WHEN** uma comanda é criada para uma mesa com status `LIVRE`
- **THEN** o status da mesa muda automaticamente para `OCUPADA`

#### Scenario: Mesa fica LIVRE ao encerrar
- **WHEN** todas as comandas de uma mesa atingem status `CANCELADA` ou `PAGA`
- **THEN** os itens compartilhados da mesa são removidos
- **THEN** o status da mesa muda automaticamente para `LIVRE`

### Requirement: Autenticação e autorização de garçom
O sistema SHALL autenticar garçons via JWT, igual aos demais usuários. Garçons possuem role `GARCOM` e acessam apenas endpoints de mesa/comanda.

#### Scenario: Garçom faz login
- **WHEN** um garçom envia POST para `/login` com email e senha válidos
- **THEN** o sistema retorna 200 OK com token JWT contendo role `GARCOM`

#### Scenario: Garçom não pode acessar endpoints de delivery
- **WHEN** um garçom autenticado envia GET para `/API/V1/pedidos`
- **THEN** o sistema retorna 403 Forbidden (se houver restrição por role) ou 200 (se não houver — decidir em implementação)
