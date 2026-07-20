# Requisitos — Pedidos (Delivery)

## RF032: Criar Pedido Delivery
- **Endpoint:** `POST /API/V1/pedidos`
- **Autenticação:** JWT (role CLIENTE) ou anônimo (convidado com nome/telefone/email)
- **Body:** `{ "restauranteId": Long, "itens": [{ "produtoId": Long, "quantidade": Integer, "tamanho": "INTEIRA" | "MEIA" }], "enderecoDeEntrega": String, "observacoes": String, "tipoConsumo": "COMER_NO_LOCAL" | "LEVAR" }`
- **Resposta:** `201 Created` com `PedidoResponseDTO`
- **Validações:**
  - Restaurante deve existir
  - Endereço de entrega obrigatório para delivery
  - Produtos devem pertencer ao restaurante
  - `tipoConsumo` ignorado para delivery
- **Regras de negócio:**
  - Status inicial: `PENDENTE`
  - `subtotal` = soma dos subtotais dos itens
  - `taxaEntrega` = valor do frete da região de entrega
  - `valorTotal` = `subtotal + taxaEntrega`
  - Gera `codigoPedido` único

## RF033: Criar Pedido Presencial (via Comanda)
- Pedidos com `origem = PRESENCIAL` são criados **indiretamente** ao criar comandas
- `taxaEntrega` = 0
- `enderecoDeEntrega` = null
- `tipoConsumo` pode ser `COMER_NO_LOCAL` (padrão) ou `LEVAR`

## RF034: Listar Pedidos
- **Endpoint:** `GET /API/V1/pedidos`
- **Autenticação:** JWT (qualquer role)
- **Parâmetros:** `?restauranteId={id}` (para donos/garçons) ou automático pelo usuário logado
- **Resposta:** `200 OK` com `List<PedidoResponseDTO>`

## RF035: Buscar Pedido por ID
- **Endpoint:** `GET /API/V1/pedidos/{id}`
- **Autenticação:** JWT (qualquer role)
- **Resposta:** `200 OK` com `PedidoResponseDTO`

## RF036: Atualizar Status do Pedido
- **Endpoint:** `PUT /API/V1/pedidos/{id}/status`
- **Autenticação:** JWT (role DONO_RESTAURANTE ou GARCOM)
- **Body:** `{ "status": "CONFIRMADO" | "EM_PREPARACAO" | "SAIU_PARA_ENTREGA" | "ENTREGUE" | "CANCELADO" }`
- **Resposta:** `200 OK` com `PedidoResponseDTO`

## RF037: Máquina de Estados do Pedido
- `PENDENTE` → `CONFIRMADO` → `EM_PREPARACAO` → `SAIU_PARA_ENTREGA` → `ENTREGUE`
- Qualquer estado → `CANCELADO`
- Transições apenas unidirecionais (não volta para estado anterior)

## RF038: Região de Entrega
- Cada restaurante define regiões de entrega com `nome` e `valorFrete`
- Ao criar pedido delivery, o sistema identifica a região pelo endereço e aplica a taxa
- `GET /API/V1/regioes-entrega/restaurante/{restauranteId}` lista as regiões
