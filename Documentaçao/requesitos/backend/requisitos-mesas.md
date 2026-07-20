# Requisitos — Mesas

## RF001: Criar Mesa
- **Endpoint:** `POST /API/V1/mesas`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "restauranteId": Long, "nomeCliente": String }`
- **Validações:**
  - `restauranteId` obrigatório e deve pertencer ao restaurante do usuário logado
  - `nomeCliente` obrigatório e não vazio
- **Resposta:** `201 Created` com `MesaResponseDTO` (id, nomeCliente, status=LIVRE, dataAbertura, restauranteId, restauranteNome, itensCompartilhados=[])
- **Regras de negócio:**
  - A mesa é criada com status `LIVRE`
  - A lista `itensCompartilhados` é inicializada como vazia
  - A data de abertura é registrada como o momento atual

## RF002: Listar Mesas
- **Endpoint:** `GET /API/V1/mesas`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Parâmetros:** Nenhum (o restaurante é identificado pelo usuário logado)
- **Resposta:** `200 OK` com `List<MesaResponseDTO>`
- **Regras de negócio:**
  - Retorna **apenas** as mesas do restaurante do usuário logado
  - Pode retornar lista vazia
  - Ordenação: padrão do repositório (por ID)

## RF003: Buscar Mesa por ID
- **Endpoint:** `GET /API/V1/mesas/{id}`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Resposta:** `200 OK` com `MesaResponseDTO`
- **Validações:**
  - Se a mesa não existir → `404 Not Found`
  - Se a mesa pertencer a outro restaurante → `404 Not Found` (não expõe existência)
- **Campos retornados:**
  - Inclui `itensCompartilhados` com a lista atual de itens compartilhados da mesa

## RF004: Atualizar Mesa
- **Endpoint:** `PUT /API/V1/mesas/{id}`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "restauranteId": Long, "nomeCliente": String }`
- **Resposta:** `200 OK` com `MesaResponseDTO`
- **Validações:**
  - Mesa deve existir
  - Mesa deve pertencer ao restaurante do usuário logado
- **Regras de negócio:**
  - Apenas o campo `nomeCliente` é atualizado
  - `restauranteId` no body deve ser o mesmo do restaurante do usuário

## RF005: Deletar Mesa
- **Endpoint:** `DELETE /API/V1/mesas/{id}`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Resposta:** `204 No Content`
- **Validações:**
  - Mesa deve existir
  - Mesa deve pertencer ao restaurante do usuário logado
  - Mesa **não pode** ter comandas com status `ABERTA` ou `AGUARDANDO_PIX`
  - Se houver comandas abertas → `400 Bad Request` "Existe(m) comanda(s) aberta(s) nesta mesa. Feche ou cancele antes de remover."

## RF006: Adicionar Item Compartilhado à Mesa
- **Endpoint:** `POST /API/V1/mesas/{mesaId}/compartilhados`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Body:** `{ "produtoId": Long, "quantidade": Integer, "observacao": String (opcional) }`
- **Resposta:** `201 Created` com `ItemCompartilhadoResponseDTO`
- **Validações:**
  - Mesa deve existir e pertencer ao restaurante do usuário
  - Produto deve existir
- **Regras de negócio:**
  - O `precoUnitario` é copiado do produto no momento da adição (snapshot)
  - O item fica vinculado à mesa, **não** a nenhuma comanda específica

## RF007: Listar Itens Compartilhados
- **Endpoint:** `GET /API/V1/mesas/{mesaId}/compartilhados`
- **Autenticação:** JWT (qualquer role vinculada ao restaurante)
- **Resposta:** `200 OK` com `List<ItemCompartilhadoResponseDTO>`

## RF008: Remover Item Compartilhado
- **Endpoint:** `DELETE /API/V1/mesas/{mesaId}/compartilhados/{itemId}`
- **Autenticação:** JWT (role GARCOM ou DONO_RESTAURANTE)
- **Resposta:** `204 No Content`
- **Validações:**
  - Item deve existir e pertencer à mesa especificada
  - Mesa deve pertencer ao restaurante do usuário

## RF009: Isolamento entre Restaurantes
- Um garçom/dono **só enxerga e opera** mesas do próprio restaurante
- Tentativa de acessar mesa de outro restaurante retorna `404 Not Found`
- A listagem de mesas já filtra automaticamente pelo restaurante do usuário logado

## RF010: Encerrar Mesa
- **Endpoint:** `POST /API/V1/mesas/{id}/encerrar`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Resposta:** `200 OK` com `MesaResponseDTO` (status agora `LIVRE`)
- **Regras de negócio:**
  - Comandas com status `ABERTA` ou `AGUARDANDO_PIX` são transicionadas para `CANCELADA`
  - A mesa volta para status `LIVRE`
  - Dados históricos (comandas `FECHADA`, `PAGA`, `CANCELADA`) são **preservados**
  - Apenas o dono do restaurante pode encerrar mesas
