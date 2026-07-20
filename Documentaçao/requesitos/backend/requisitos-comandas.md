# Requisitos — Comandas

## RF010: Criar Comanda
- **Endpoint:** `POST /API/V1/mesas/{mesaId}/comandas`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "clienteNome": String, "itens": [{ "produtoId": Long, "quantidade": Integer }] }`
- **Resposta:** `201 Created` com `ComandaResponseDTO`
- **Validações:**
  - Mesa deve existir e pertencer ao restaurante do usuário
  - Se a mesa estiver LIVRE, muda para OCUPADA automaticamente
  - `clienteNome` obrigatório
  - `itens` é opcional (pode ser array vazio)
  - Cada item: `produtoId` obrigatório, `quantidade` > 0
- **Regras de negócio:**
  - Status inicial: `ABERTA`
  - `valorTotal` calculado como `soma(precoUnitario * quantidade)` de cada item
  - `precoUnitario` de cada item é copiado do produto no momento (snapshot)
  - Itens são **individuais** da comanda (não há flag compartilhado)

## RF011: Listar Comandas de uma Mesa
- **Endpoint:** `GET /API/V1/comandas?mesaId={mesaId}`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Resposta:** `200 OK` com `List<ComandaResponseDTO>`
- **Regras de negócio:**
  - Retorna apenas comandas da mesa especificada
  - Ordenadas da mais recente para a mais antiga

## RF012: Buscar Comanda por ID
- **Endpoint:** `GET /API/V1/comandas/{id}`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Resposta:** `200 OK` com `ComandaResponseDTO`
- **Validações:**
  - Comanda deve existir
  - Comanda deve pertencer ao restaurante do usuário logado

## RF013: Adicionar Item na Comanda
- **Endpoint:** `POST /API/V1/comandas/{id}/itens`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "produtoId": Long, "quantidade": Integer }`
- **Resposta:** `200 OK` com `ComandaItemResponseDTO`
- **Validações:**
  - Comanda deve existir e estar `ABERTA`
  - Comanda deve pertencer ao restaurante do usuário
  - Produto deve existir
- **Regras de negócio:**
  - `precoUnitario` copiado do produto (snapshot)
  - `valorTotal` da comanda é atualizado somando o subtotal do novo item

## RF014: Atualizar Item da Comanda
- **Endpoint:** `PUT /API/V1/comandas/{comandaId}/itens/{itemId}`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "produtoId": Long, "quantidade": Integer }`
- **Resposta:** `200 OK` com `ComandaItemResponseDTO`
- **Validações:**
  - Item deve existir e pertencer à comanda
  - Comanda deve estar `ABERTA`
- **Regras de negócio:**
  - Quantidade é atualizada
  - `valorTotal` da comanda é recalculado

## RF015: Remover Item da Comanda
- **Endpoint:** `DELETE /API/V1/comandas/{comandaId}/itens/{itemId}`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Resposta:** `204 No Content`
- **Validações:**
  - Item deve existir e pertencer à comanda
  - Comanda deve estar `ABERTA`
- **Regras de negócio:**
  - `valorTotal` da comanda é recalculado subtraindo o valor do item removido

## RF016: Rateio de Item Compartilhado
- **Endpoint:** `POST /API/V1/comandas/{id}/rateio`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "produtoId": Long, "valorPago": BigDecimal }`
- **Resposta:** `200 OK` com `ComandaResponseDTO`
- **Validações:**
  - Comanda deve existir e estar `ABERTA`
  - Produto deve existir e estar no pool de itens compartilhados da mesa
  - `valorPago` deve ser positivo (> 0)
  - `valorPago` não pode exceder o saldo restante do item na mesa
- **Regras de negócio:**
  - Saldo restante = `(quantidade * precoUnitario)` do item na mesa - `soma(valorPago)` de todos os rateios do mesmo produto em qualquer comanda da mesa
  - Se saldo insuficiente → `400 Bad Request` com mensagem do saldo disponível
  - A comanda **não** fecha automaticamente após rateio

## RF017: Fechar Comanda (Presencial)
- **Endpoint:** `POST /API/V1/comandas/{id}/fechar`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "formaPagamento": "DINHEIRO" | "CARTAO_CREDITO" | "MAQUININHA" }`
- **Resposta:** `200 OK` com `ComandaResponseDTO`
- **Validações:**
  - Comanda deve existir e estar `ABERTA`
  - `formaPagamento` não pode ser `PIX` (usa endpoint próprio)
  - `formaPagamento` não pode ser nulo
- **Regras de negócio:**
  - Status muda para `PAGA`
  - `dataFechamento` é registrada
  - Se for a última comanda PAGA da mesa, mesa volta para `LIVRE`

## RF018: Máquina de Estados da Comanda
- `ABERTA` → (fechar presencial) → `PAGA`
- `ABERTA` → (gerar QR Code Pix) → `AGUARDANDO_PIX` → (webhook confirma) → `PAGA`
- `ABERTA` ou `AGUARDANDO_PIX` → (encerrar mesa) → `CANCELADA`
- Uma comanda `AGUARDANDO_PIX` pode ter o QR Code **regenerado** se a transação anterior expirar
- Comandas `CANCELADA` preservam dados históricos (não são removidas)
