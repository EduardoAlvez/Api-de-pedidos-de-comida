## ADDED Requirements

### Requirement: Garçom pode adicionar item compartilhado à mesa
O sistema SHALL permitir que um garçom adicione um item compartilhado ao pool da mesa.
- Endpoint: `POST /API/V1/mesas/{mesaId}/compartilhados`
- Autenticação: JWT obrigatório (role GARCOM ou DONO_RESTAURANTE)
- Body: `{"produtoId": Long, "quantidade": Integer, "observacao": String (opcional), "tamanho": "INTEIRA" | "MEIA" (opcional, default INTEIRA)}`
- O item compartilhado é vinculado à mesa, não a nenhuma comanda
- O preço unitário é copiado do produto no momento da adição (usa `precoMeia` se `tamanho == MEIA`)
- Retorna `201 Created` com o item criado
- Valida que a mesa existe e pertence ao restaurante do garçom logado
- Valida que o produto existe
- Valida que se `tamanho == MEIA`, o produto possui `precoMeia` preenchido

#### Scenario: Garçom adiciona item compartilhado com sucesso
- **WHEN** garçom autenticado envia POST para `/API/V1/mesas/1/compartilhados` com `{"produtoId": 2, "quantidade": 1}`
- **THEN** sistema retorna `201 Created` com o item compartilhado criado, contendo `id`, `produtoId`, `nomeProduto`, `quantidade`, `precoUnitario`, `observacao`, `tamanho`

#### Scenario: Garçom adiciona item compartilhado meia porção
- **WHEN** garçom autenticado envia POST para `/API/V1/mesas/1/compartilhados` com `{"produtoId": 2, "quantidade": 1, "tamanho": "MEIA"}`
- **THEN** sistema retorna `201 Created` e `precoUnitario` reflete o `precoMeia` do produto

#### Scenario: Garçom tenta adicionar meia porção sem precoMeia
- **WHEN** garçom autenticado envia POST com `tamanho: "MEIA"` para um produto sem `precoMeia`
- **THEN** sistema retorna `400 Bad Request` com mensagem "produto não oferece meia porção"

#### Scenario: Garçom tenta adicionar item em mesa de outro restaurante
- **WHEN** garçom de outro restaurante tenta adicionar item em mesa
- **THEN** sistema retorna `404 Not Found`

### Requirement: Listar itens compartilhados da mesa
O sistema SHALL listar todos os itens compartilhados de uma mesa.
- Endpoint: `GET /API/V1/mesas/{mesaId}/compartilhados`
- Autenticação: JWT obrigatório (qualquer role vinculada ao restaurante)
- Retorna `200 OK` com lista de itens (pode ser vazia)

#### Scenario: Listar itens compartilhados de mesa com itens
- **WHEN** garçom autenticado envia GET para `/API/V1/mesas/1/compartilhados`
- **THEN** sistema retorna `200 OK` com array de itens compartilhados

#### Scenario: Listar itens de mesa sem itens compartilhados
- **WHEN** garçom autenticado envia GET para `/API/V1/mesas/1/compartilhados` e não há itens
- **THEN** sistema retorna `200 OK` com array vazio

### Requirement: Atualizar item compartilhado da mesa
O sistema SHALL permitir atualizar um item compartilhado do pool da mesa.
- Endpoint: `PUT /API/V1/mesas/{mesaId}/compartilhados/{itemId}`
- Autenticação: JWT obrigatório (role GARCOM ou DONO_RESTAURANTE)
- Body: `{"produtoId": Long (opcional), "quantidade": Integer (opcional), "observacao": String (opcional), "tamanho": "INTEIRA" | "MEIA" (opcional)}`
- Se `produtoId` for alterado, o preço unitário é recopiado do novo produto (considerando `tamanho`)
- Se `tamanho` for alterado sem trocar produto, o preço unitário é recalculado (usa `precoMeia` se MEIA)
- Se `quantidade` for fornecida, atualiza; caso contrário mantém o valor anterior
- Retorna `200 OK` com o item atualizado
- Valida que o item pertence à mesa e que a mesa existe

#### Scenario: Garçom atualiza item compartilhado com sucesso
- **WHEN** garçom autenticado envia PUT para `/API/V1/mesas/1/compartilhados/1` com `{"quantidade": 3}`
- **THEN** sistema retorna `200 OK` com o item atualizado, quantidade refletindo o novo valor

#### Scenario: Garçom troca produto do item compartilhado
- **WHEN** garçom autenticado envia PUT para `/API/V1/mesas/1/compartilhados/1` com `{"produtoId": 5}`
- **THEN** sistema retorna `200 OK` e o `precoUnitario` é atualizado com o preço do novo produto

#### Scenario: Garçom altera tamanho do item compartilhado
- **WHEN** garçom autenticado envia PUT para `/API/V1/mesas/1/compartilhados/1` com `{"tamanho": "MEIA"}`
- **THEN** sistema retorna `200 OK` e o `precoUnitario` é recalculado para o `precoMeia`

### Requirement: Remover item compartilhado da mesa
O sistema SHALL permitir remover um item compartilhado do pool da mesa.
- Endpoint: `DELETE /API/V1/mesas/{mesaId}/compartilhados/{itemId}`
- Autenticação: JWT obrigatório (role GARCOM ou DONO_RESTAURANTE)
- Retorna `204 No Content`
- Valida que o item pertence à mesa

#### Scenario: Garçom remove item compartilhado com sucesso
- **WHEN** garçom autenticado envia DELETE para `/API/V1/mesas/1/compartilhados/1`
- **THEN** sistema retorna `204 No Content` e o item é removido

### Requirement: MesaResponseDTO inclui itens compartilhados
O sistema SHALL retornar os itens compartilhados ao buscar/listar mesas.
- Endpoint: `GET /API/V1/mesas` e `GET /API/V1/mesas/{id}`
- O response inclui campo `itensCompartilhados: ItemCompartilhadoResponseDTO[]`
- ItemCompartilhadoResponseDTO contém: `id`, `produtoId`, `nomeProduto`, `quantidade`, `precoUnitario`, `observacao`, `tamanho`

#### Scenario: Buscar mesa exibe itens compartilhados
- **WHEN** garçom busca mesa por ID via `GET /API/V1/mesas/1`
- **THEN** response inclui `itensCompartilhados` com a lista de itens compartilhados da mesa

### Requirement: ComandaItem possui tamanho (sem flag compartilhado)
O sistema SHALL remover o campo `compartilhado` de `ComandaItem` e adicionar `tamanho`.
- `ComandaItem.compartilhado` é removido
- `ComandaItem.tamanho` é adicionado (TamanhoPorcao: INTEIRA | MEIA, default INTEIRA)
- `ComandaItemResponseDTO.compartilhado` é removido, `tamanho` é adicionado
- `ComandaItemRequestDTO.compartilhado` é removido, `tamanho` é adicionado (opcional, default INTEIRA)
- `ComandaItem` usa `produto.preco` se `INTEIRA`, ou `produto.precoMeia` se `MEIA`

#### Scenario: Criar comanda sem campo compartilhado
- **WHEN** garçom cria comanda via `POST /API/V1/mesas/1/comandas` com body sem `compartilhado`
- **THEN** sistema cria comanda normalmente, itens são considerados individuais

#### Scenario: Criar comanda com item meia porção
- **WHEN** garçom cria comanda via `POST /API/V1/mesas/1/comandas` com item `{"produtoId": 2, "quantidade": 1, "tamanho": "MEIA"}`
- **THEN** sistema cria comanda e o `precoUnitario` do item usa `produto.precoMeia`

#### Scenario: Adicionar item meia porção na comanda
- **WHEN** garçom adiciona item via `POST /API/V1/comandas/1/itens` com `{"produtoId": 2, "quantidade": 1, "tamanho": "MEIA"}`
- **THEN** sistema retorna `201 Created` com o item e `precoUnitario` reflete o `precoMeia`

### Requirement: Rateio consulta itens compartilhados da mesa
O sistema SHALL alterar o rateio para consultar `mesa.itensCompartilhados` em vez de `comanda.itens[compartilhado=true]`.
- Endpoint: `POST /API/V1/comandas/{id}/rateio` (sem alteração de rota)
- A validação de saldo usa o pool de itens compartilhados da mesa
- O registro do rateio (`ComandaRateio`) permanece vinculado à comanda

#### Scenario: Rateio de item compartilhado com sucesso
- **WHEN** garçom envia rateio para item compartilhado da mesa
- **THEN** sistema valida contra o pool da mesa e registra o rateio na comanda

#### Scenario: Rateio de item que não está nos compartilhados
- **WHEN** garçom tenta ratear um item que não está no pool da mesa
- **THEN** sistema valida que o item não está compartilhado (comportamento a definir — pode permitir rateio de qualquer item da mesa ou rejeitar)

### Requirement: Limpar itens compartilhados ao fechar última comanda
O sistema SHALL remover automaticamente todos os `ItemCompartilhado` de uma mesa quando a última comanda for fechada.
- Disparado no endpoint `POST /API/V1/comandas/{id}/fechar`
- Quando `comandaRepository.countByMesa_IdAndStatus(mesaId, ABERTA) == 0` e `countByMesa_IdAndStatus(mesaId, AGUARDANDO_PIX) == 0`
- Executa `itemCompartilhadoRepository.deleteAllByMesa_Id(mesaId)`
- A mesa só então tem seu status alterado para `LIVRE`
- Dados históricos das comandas e rateios NÃO são afetados

#### Scenario: Fechar última comanda limpa itens compartilhados
- **GIVEN** mesa com 1 comanda ABERTA e 2 itens compartilhados
- **WHEN** garçom fecha a comanda via `POST /API/V1/comandas/1/fechar`
- **THEN** itens compartilhados da mesa são removidos, mesa volta para `LIVRE`

#### Scenario: Fechar comanda com outras abertas mantém compartilhados
- **GIVEN** mesa com 2 comandas ABERTAS e itens compartilhados
- **WHEN** garçom fecha apenas 1 comanda via `POST /API/V1/comandas/1/fechar`
- **THEN** itens compartilhados permanecem na mesa, mesa continua `OCUPADA`
