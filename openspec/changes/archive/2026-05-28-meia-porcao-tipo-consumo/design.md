## Context

Atualmente o sistema trata todos os itens como porção inteira e não diferencia o tipo de consumo em pedidos presenciais. O modelo real de comandas físicas mostra que:
- Clientes pedem meia porção com preço definido pelo estabelecimento (não é metade do valor automaticamente)
- Garçons precisam sinalizar se o pedido é para consumo no local ("Comer Aqui!") ou viagem ("Via")
- O Pedido entity já possui campo `origem` (DELIVERY/PRESENCIAL) mas ele não é populado via DTO
- ItemPedido usa `precoUnitario` para guardar o preço no momento da compra

## Goals / Non-Goals

**Goals:**
- Adicionar `precoMeia` (nullable) na entidade Produto — preço definido pelo restaurante
- Adicionar `tamanho` (INTEIRA | MEIA) na entidade ItemPedido
- Adicionar `tipoConsumo` (COMER_AQUI | VIAGEM) na entidade Pedido para pedidos PRESENCIAL
- Adicionar campo `origem` no PedidoRequestDTO para permitir criar pedidos DELIVERY ou PRESENCIAL
- Calcular subtotal do item conforme o tamanho: INTEIRA → `preco`, MEIA → `precoMeia`
- Validar que item MEIA só é aceito se produto tiver `precoMeia` preenchido
- Propagar os novos campos nos DTOs de request e response

**Non-Goals:**
- Não alterar a lógica de frete/região de entrega
- Não alterar o fluxo de pagamento
- Não criar endpoints novos (apenas campos novos nos DTOs existentes)
- Não implementar histórico de preçoMeia (o preço vigente no momento da compra é salvo em `precoUnitario`)

## Decisions

### 1. precoMeia como campo nullable no Produto (não tabela separada)
- **Alternativa considerada**: tabela `TamanhoProduto` com tipo + preço
- **Decisão**: campo simples `precoMeia` nullable — mais simples, sem join extra, sem nova entidade. Cada produto tem no máximo 2 tamanhos (inteira + meia), não justifica uma entidade separada.

### 2. TamanhoPorcao como enum separado (não boolean "isMeia")
- **Alternativa considerada**: `boolean meia` no ItemPedido
- **Decisão**: enum `TamanhoPorcao` com `INTEIRA` e `MEIA` — mais extensível se no futuro surgir "BROTO" ou "FAMILIA", e mais explícito na leitura do código

### 3. TipoConsumo como enum no Pedido (não campo separado na Comanda)
- **Alternativa considerada**: campo `tipoConsumo` na Mesa ou na Comanda
- **Decisão**: o tipo de consumo pertence ao pedido (cada pedido pode ter um destino diferente), não à mesa. Uma mesa pode ter pedidos COMER_AQUI e VIAGEM simultaneamente.

### 4. OrigemPedido adicionado ao PedidoRequestDTO
- Atualmente o entity `Pedido` já tem `origem` mas o DTO de request não, então o campo nunca é populado na criação. Vamos adicionar `origem` ao DTO.

### 5. DELIVERY ignora tipoConsumo
- Se `origem = DELIVERY` o campo `tipoConsumo` é ignorado (delivery é sempre externo). Se `origem = PRESENCIAL` e `tipoConsumo` não informado, assume `COMER_AQUI` como padrão.

### 6. Preço salvo no momento da compra
- `ItemPedido.precoUnitario` já armazena o preço no momento da compra — vamos continuar usando esta estratégia. Se `tamanho = MEIA`, `precoUnitario = produto.precoMeia`; se `INTEIRA`, `precoUnitario = produto.preco`.

## Risks / Trade-offs

- **[Migração de dados]** Produtos existentes não terão `precoMeia` — campo será null, e tentar usar MEIA com esses produtos retornará erro 400 (comportamento esperado)
- **[Relatório financeiro]** Relatórios históricos podem se tornar mais complexos com dois preços possíveis por produto. Mitigação: `precoUnitario` no ItemPedido já captura o preço real, então relatórios devem olhar para ItemPedido, não Produto
- **[API Change]** Clientes da API que criam pedidos precisarão incluir `origem` no request (campo obrigatório) — breaking change suave pois o campo será exigido
- **[Testes]** A lógica de precificação condicional (se MEIA usa precoMeia) precisa de cobertura de teste para ambos os caminhos
