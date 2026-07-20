## Why

O sistema atualmente só trabalha com porções inteiras e não diferencia se o pedido presencial é para consumo no local ou viagem. As comandas físicas do estabelecimento real mostram que ambas as funcionalidades são necessárias: o cliente pode pedir meia porção (com preço próprio definido pelo restaurante) e o garçom precisa sinalizar se é "Comer Aqui!" ou "Via".

## What Changes

1. **Produto** ganha campo `precoMeia` (nullable) — se preenchido, o produto oferece meia porção com preço próprio
2. **ItemPedido** ganha campo `tamanho` (`INTEIRA | MEIA`) — default `INTEIRA`
3. **Pedido** (quando `origem = PRESENCIAL`) ganha campo `tipoConsumo` (`COMER_AQUI | VIA`)
4. Lógica de cálculo do pedido considera o tamanho do item para definir o preço
5. Validações: meia porção só é permitida se o produto tiver `precoMeia` definido
6. Pedido ganha `tipoConsumo` (`COMER_AQUI | VIAGEM`) — apenas para pedidos com `origem = PRESENCIAL`

## Capabilities

### New Capabilities
- `meia-porcao`: controle de meia porção por produto com preço definido pelo estabelecimento, e seleção de tamanho (inteira/meia) nos itens do pedido
- `tipo-consumo`: diferenciação entre consumo no local (COMER_AQUI) e viagem (VIA) para pedidos presenciais

### Modified Capabilities
<!-- Nenhuma spec existente tem seus requisitos alterados — as funcionalidades são aditivas -->

## Impact

- **Entity**: `Produto` (+precoMeia), `ItemPedido` (+tamanho), `Pedido` (+tipoConsumo)
- **DTOs**: `ProdutoRequestDTO`, `ProdutoResponseDTO`, `ItemPedidoRequestDTO`, `ItemPedidoResponseDTO`, `PedidoRequestDTO`, `PedidoResponseDTO`
- **Service**: `PedidoService.calcularTotal()` — lógica de precificação por tamanho; validações de meia porção
- **Testes**: novos cenários de meia porção e tipo de consumo
- **Seed/DataLoader**: possível inclusão de produtos com `precoMeia`
