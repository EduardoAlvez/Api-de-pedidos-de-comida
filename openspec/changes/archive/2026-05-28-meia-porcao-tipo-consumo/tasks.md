## 1. Enums

- [x] 1.1 Criar `TamanhoPorcao.java` no pacote `models/enums/` com `INTEIRA` e `MEIA`
- [x] 1.2 Criar `TipoConsumo.java` no pacote `models/enums/` com `COMER_AQUI` e `VIA`

## 2. Entity — Produto

- [x] 2.1 Adicionar campo `precoMeia` (`BigDecimal`, nullable) na entidade `Produto.java`

## 3. Entity — ItemPedido

- [x] 3.1 Adicionar campo `tamanho` (`TamanhoPorcao`, default `INTEIRA`, `@Enumerated(EnumType.STRING)`) na entidade `ItemPedido.java`

## 4. Entity — Pedido

- [x] 4.1 Adicionar campo `tipoConsumo` (`TipoConsumo`, nullable, `@Enumerated(EnumType.STRING)`) na entidade `Pedido.java`

## 5. DTOs — Produto

- [x] 5.1 Adicionar campo `precoMeia` (`BigDecimal`, opcional) no `ProdutoRequestDTO.java`
- [x] 5.2 Adicionar campo `precoMeia` (`BigDecimal`, nullable) no `ProdutoResponseDTO.java`

## 6. DTOs — ItemPedido

- [x] 6.1 Adicionar campo `tamanho` (`TamanhoPorcao`, opcional) no `ItemPedidoRequestDTO.java`
- [x] 6.2 Adicionar campo `tamanho` (`TamanhoPorcao`) no `ItemPedidoResponseDTO.java`

## 7. DTOs — Pedido

- [x] 7.1 Adicionar campo `origem` (`OrigemPedido`, obrigatório) no `PedidoRequestDTO.java`
- [x] 7.2 Adicionar campo `tipoConsumo` (`TipoConsumo`, opcional) no `PedidoRequestDTO.java`
- [x] 7.3 Remover `@NotEmpty` do campo `enderecoDeEntrega` no `PedidoRequestDTO.java` (validação passará para o service)
- [x] 7.4 Adicionar campo `tipoConsumo` (`TipoConsumo`) no `PedidoResponseDTO.java`

## 8. Service — PedidoService

- [x] 8.1 Atualizar `criar()` para receber e setar `origem` e `tipoConsumo` no Pedido entity
- [x] 8.2 Adicionar validação: se `origem = DELIVERY` e `enderecoDeEntrega` vazio → erro
- [x] 8.3 Adicionar validação: se `origem = PRESENCIAL` e `tipoConsumo` null → default `COMER_AQUI`
- [x] 8.4 Atualizar lógica de precificação dos itens: se `tamanho = MEIA` usar `produto.precoMeia` como `precoUnitario` no lugar de `produto.preco`
- [x] 8.5 Adicionar validação: se `tamanho = MEIA` e `produto.precoMeia == null` → `ValidacaoNegocioException` ("Produto não oferece meia porção")
- [x] 8.6 Atualizar `toItemPedidoResponseDTO()` para copiar campo `tamanho`
- [x] 8.7 Atualizar `toResponseDTO()` para copiar campo `tipoConsumo`

## 9. Testes

- [x] 9.1 Atualizar `PedidoServiceTest` com cenários de meia porção (MEIA com precoMeia, MEIA sem precoMeia → erro, INTEIRA default, itens mistos)
- [x] 9.2 Adicionar cenários de `tipoConsumo` no `PedidoServiceTest` (default COMER_AQUI, VIA explícito, DELIVERY ignora)
- [x] 9.3 Atualizar `PedidoControllerTest` com cenários de integração para meia porção e tipo consumo
- [ ] 9.4 ~~Atualizar `ProdutoServiceTest` com cenários de precoMeia (criação, atualização, listagem)~~ — arquivo não existe
