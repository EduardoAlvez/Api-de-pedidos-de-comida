# Matriz de Rastreabilidade — Backend (API REST)

## Legenda

| Tipo | Significado |
|------|-------------|
| **Direta** | Elemento UML corresponde 1:1 a um arquivo/componente de código |
| **Parcial** | Elemento UML corresponde parcialmente, com adaptações |
| **Indireta** | Elemento UML não tem correspondência direta, mas está representado indiretamente |
| **Ausente** | Elemento UML não foi implementado |

---

## 1. Classes — Entidades JPA

*Diagrama de origem: `docs/diagramas/Backend/classes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Endpoint / Método | Correspondência |
|----|--------------|---------------------|-------------------|----------------|
| BC1 | `Mesa` (id, nomeCliente, status, dataAbertura, restaurante, itensCompartilhados) | `models/Mesa.java` | CRUD via MesaService | Direta |
| BC2 | `Comanda` (id, mesa, garcom, clienteNome, status, dataAbertura, dataFechamento, valorTotal, itens, rateios) | `models/Comanda.java` | CRUD via ComandaService | Direta |
| BC3 | `ComandaItem` (id, comanda, produto, quantidade, precoUnitario) | `models/ComandaItem.java` | POST/PUT/DELETE /comandas/{id}/itens | Direta |
| BC4 | `ComandaRateio` (id, comanda, produto, valorPago, dataPagamento) | `models/ComandaRateio.java` | POST /comandas/{id}/rateio | Direta |
| BC5 | `ItemCompartilhado` (id, mesa, produto, quantidade, precoUnitario, observacao) | `models/ItemCompartilhado.java` | POST/GET/PUT/DELETE /mesas/{id}/compartilhados | Direta |
| BC6 | `Produto` (id, nome, descricao, preco, precoMeia, categoria, imageUrl, disponivel, restaurante) | `models/Produto.java` | CRUD via ProdutoController | Direta |
| BC7 | `Usuario` (id, nome, email, telefone, senha, tipo, restaurante, restauranteTrabalho) | `models/Usuario.java` | CRUD via UsuarioController | Direta |
| BC8 | `Restaurante` (id, nome, endereco, telefone, cnpj, tipoCozinha, horarioFuncionamento, imageUrl, produtos, regioes, pedidos, usuario, garcons) | `models/Restaurante.java` | CRUD via RestauranteController | Direta |
| BC9 | `Pedido` (id, codigoPedido, dataDoPedido, status, origem, tipoConsumo, subtotal, taxaEntrega, valorTotal, enderecoDeEntrega, usuario, restaurante, pagamento, itens) | `models/Pedido.java` | CRUD via PedidoController | Direta |
| BC10 | `ItemPedido` (id, quantidade, precoUnitario, tamanho, pedido, produto) | `models/ItemPedido.java` | (criado junto com Pedido) | Direta |
| BC11 | `Pagamento` (id, valorTotal, dataDoPagamento, formaDePagamento, status, pedido) | `models/Pagamento.java` | (criado junto com Pedido) | Direta |
| BC12 | `TransacaoPix` (id, comanda, valor, qrCodeBase64, payloadCopiaCola, txId, status, dataCriacao, dataConfirmacao) | `models/TransacaoPix.java` | POST/GET /comandas/{id}/pix | Direta |
| BC13 | `RegiaoEntrega` (id, nome, valorFrete, restaurante) | `models/RegiaoEntrega.java` | CRUD via RegiaoEntregaController | Direta |

---

## 2. DTOs (Request / Response)

*Diagrama de origem: `docs/diagramas/Backend/classes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Endpoint / Método | Correspondência |
|----|--------------|---------------------|-------------------|----------------|
| BD1 | `MesaRequestDTO` (restauranteId, nomeCliente) | `dtos/MesaRequestDTO.java` | POST/PUT /API/V1/mesas | Direta |
| BD2 | `MesaResponseDTO` (id, nomeCliente, status, dataAbertura, restauranteId, restauranteNome, itensCompartilhados) | `dtos/MesaResponseDTO.java` | GET /API/V1/mesas | Direta |
| BD3 | `ComandaRequestDTO` (clienteNome, itens) | `dtos/ComandaRequestDTO.java` | POST /API/V1/mesas/{mesaId}/comandas | Direta |
| BD4 | `ComandaResponseDTO` (id, mesaId, garcomId, clienteNome, status, valorTotal, itens, rateios) | `dtos/ComandaResponseDTO.java` | GET /API/V1/comandas | Direta |
| BD5 | `ComandaItemRequestDTO` (produtoId, quantidade) | `dtos/ComandaItemRequestDTO.java` | POST/PUT /API/V1/comandas/{id}/itens | Direta |
| BD6 | `ComandaItemResponseDTO` (id, produtoId, nomeProduto, quantidade, precoUnitario, subtotal) | `dtos/ComandaItemResponseDTO.java` | — | Direta |
| BD7 | `ItemCompartilhadoRequestDTO` (produtoId, quantidade, observacao) | `dtos/ItemCompartilhadoRequestDTO.java` | POST/PUT /API/V1/mesas/{id}/compartilhados | Direta |
| BD8 | `ItemCompartilhadoResponseDTO` (id, produtoId, nomeProduto, quantidade, precoUnitario, observacao) | `dtos/ItemCompartilhadoResponseDTO.java` | GET /API/V1/mesas/{id}/compartilhados | Direta |
| BD9 | `ComandaRateioResponseDTO` (id, produtoId, nomeProduto, valorPago, dataPagamento) | `dtos/ComandaRateioResponseDTO.java` | — | Direta |
| BD10 | `RateioRequestDTO` (produtoId, valorPago) | `dtos/RateioRequestDTO.java` | POST /API/V1/comandas/{id}/rateio | Direta |
| BD11 | `LoginRequestDTO` (email, senha) | `dtos/LoginRequestDTO.java` | POST /login | Direta |
| BD12 | `FecharComandaRequestDTO` (formaPagamento) | `dtos/FecharComandaRequestDTO.java` | POST /API/V1/comandas/{id}/fechar | Direta |

---

## 3. Enums

*Diagrama de origem: `docs/diagramas/Backend/classes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Correspondência |
|----|--------------|---------------------|----------------|
| BE1 | `StatusMesa` (LIVRE, OCUPADA) | `models/enums/StatusMesa.java` | Direta |
| BE2 | `StatusComanda` (ABERTA, FECHADA, AGUARDANDO_PIX, PAGA) | `models/enums/StatusComanda.java` | Direta |
| BE3 | `Role` (CLIENTE, GARCOM, DONO_RESTAURANTE) | `models/enums/Role.java` | Direta |
| BE4 | `StatusPedido` (PENDENTE, CONFIRMADO, EM_PREPARACAO, SAIU_PARA_ENTREGA, ENTREGUE, CANCELADO) | `models/enums/StatusPedido.java` | Direta |
| BE5 | `FormaPagamento` (PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO) | `models/enums/FormaPagamento.java` | Direta |
| BE6 | `StatusPagamento` (PENDENTE, APROVADO, RECUSADO, CANCELADO) | `models/enums/StatusPagamento.java` | Direta |
| BE7 | `OrigemPedido` (DELIVERY, PRESENCIAL) | `models/enums/OrigemPedido.java` | Direta |
| BE8 | `TipoConsumo` (COMER_NO_LOCAL, LEVAR) | `models/enums/TipoConsumo.java` | Direta |
| BE9 | `TamanhoPorcao` (INTEIRA, MEIA) | `models/enums/TamanhoPorcao.java` | Direta |
| BE10 | `StatusTransacaoPix` (PENDENTE, APROVADO, RECUSADO, EXPIRADO) | `models/enums/StatusTransacaoPix.java` | Direta |

---

## 4. Controllers — Endpoints REST

*Diagrama de origem: `docs/diagramas/Backend/pacotes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Endpoint REST | Correspondência |
|----|--------------|---------------------|---------------|----------------|
| C01 | efetuarLogin() | `AutenticacaoController.java:33` | POST /login | Direta |
| C02 | criarMesa() | `MesaController.java:40` | POST /API/V1/mesas | Direta |
| C03 | listarMesas() | `MesaController.java:48` | GET /API/V1/mesas | Direta |
| C04 | buscarMesaPorId() | `MesaController.java:55` | GET /API/V1/mesas/{id} | Direta |
| C05 | atualizarMesa() | `MesaController.java:62` | PUT /API/V1/mesas/{id} | Direta |
| C06 | deletarMesa() | `MesaController.java:71` | DELETE /API/V1/mesas/{id} | Direta |
| C07 | adicionarItemCompartilhado() | `MesaController.java:83` | POST /API/V1/mesas/{mesaId}/compartilhados | Direta |
| C08 | listarItensCompartilhados() | `MesaController.java:93` | GET /API/V1/mesas/{mesaId}/compartilhados | Direta |
| C09 | atualizarItemCompartilhado() | `MesaController.java:101` | PUT /API/V1/mesas/{mesaId}/compartilhados/{itemId} | Direta |
| C10 | removerItemCompartilhado() | `MesaController.java:111` | DELETE /API/V1/mesas/{mesaId}/compartilhados/{itemId} | Direta |
| C11 | criarComanda() | `ComandaController.java:33` | POST /API/V1/mesas/{mesaId}/comandas | Direta |
| C12 | listarComandas() | `ComandaController.java:43` | GET /API/V1/comandas?mesaId= | Direta |
| C13 | buscarComanda() | `ComandaController.java:50` | GET /API/V1/comandas/{id} | Direta |
| C14 | adicionarItem() | `ComandaController.java:61` | POST /API/V1/comandas/{id}/itens | Direta |
| C15 | atualizarItem() | `ComandaController.java:71` | PUT /API/V1/comandas/{comandaId}/itens/{itemId} | Direta |
| C16 | removerItem() | `ComandaController.java:81` | DELETE /API/V1/comandas/{comandaId}/itens/{itemId} | Direta |
| C17 | rateio() | `ComandaController.java:95` | POST /API/V1/comandas/{id}/rateio | Direta |
| C18 | fecharComanda() | `ComandaController.java:104` | POST /API/V1/comandas/{id}/fechar | Direta |
| C19 | gerarQrCode() | `PixController.java:25` | POST /API/V1/comandas/{id}/pix | Direta |
| C20 | consultarStatus() | `PixController.java:31` | GET /API/V1/comandas/{id}/pix | Direta |
| C21 | receberWebhook() | `PixController.java:42` | POST /API/V1/pix/webhook | Direta |
| C22 | criarPedido() | `PedidoController.java:27` | POST /API/V1/pedidos | Direta |
| C23 | buscarPedidoPorId() | `PedidoController.java:33` | GET /API/V1/pedidos/{id} | Direta |
| C24 | listarPedidosPorUsuario() | `PedidoController.java:39` | GET /API/V1/pedidos/usuario/{usuarioId} | Direta |
| C25 | atualizarStatusPedido() | `PedidoController.java:46` | PUT /API/V1/pedidos/{id}/status | Direta |
| C26 | criarProduto() | `ProdutoController.java:26` | POST /API/V1/produtos | Direta |
| C27 | buscarProdutoPorId() | `ProdutoController.java:32` | GET /API/V1/produtos/{id} | Direta |
| C28 | atualizarProduto() | `ProdutoController.java:38` | PUT /API/V1/produtos/{id} | Direta |
| C29 | deletarProduto() | `ProdutoController.java:44` | DELETE /API/V1/produtos/{id} | Direta |
| C30 | listarProdutosPorRestaurante() | `ProdutoController.java:49` | GET /API/V1/produtos/restaurante/{restauranteId} | Direta |
| C31 | criarRestaurante() | `RestauranteController.java:28` | POST /API/V1/restaurantes | Direta |
| C32 | listarTodosRestaurantes() | `RestauranteController.java:39` | GET /API/V1/restaurantes | Direta |
| C33 | buscarRestaurantePorId() | `RestauranteController.java:50` | GET /API/V1/restaurantes/{id} | Direta |
| C34 | atualizarRestaurante() | `RestauranteController.java:61` | PUT /API/V1/restaurantes/{id} | Direta |
| C35 | deletarRestaurante() | `RestauranteController.java:74` | DELETE /API/V1/restaurantes/{id} | Direta |
| C36 | criarUsuario() | `UsuarioController.java:28` | POST /API/V1/usuarios | Direta |
| C37 | buscarUsuarioPorId() | `UsuarioController.java:39` | GET /API/V1/usuarios/{id} | Direta |
| C38 | listarTodosUsuarios() | `UsuarioController.java:49` | GET /API/V1/usuarios | Direta |
| C39 | atualizarUsuario() | `UsuarioController.java:59` | PUT /API/V1/usuarios/{id} | Direta |
| C40 | deletarUsuario() | `UsuarioController.java:72` | DELETE /API/V1/usuarios/{id} | Direta |
| C41 | vincularGarcom() | `GarcomController.java:28` | POST /API/V1/restaurantes/{id}/garcons | Direta |
| C42 | desvincularGarcom() | `GarcomController.java:40` | DELETE /API/V1/restaurantes/{id}/garcons/{usuarioId} | Direta |
| C43 | listarGarcons() | `GarcomController.java:52` | GET /API/V1/restaurantes/{id}/garcons | Direta |
| C44 | listarRegioes() | `RegiaoEntregaController.java:26` | GET /API/V1/restaurantes/{id}/regioes | Direta |
| C45 | buscarRegiaoPorId() | `RegiaoEntregaController.java:32` | GET /API/V1/restaurantes/{id}/regioes/{id} | Direta |
| C46 | criarRegiao() | `RegiaoEntregaController.java:38` | POST /API/V1/restaurantes/{id}/regioes | Direta |
| C47 | atualizarRegiao() | `RegiaoEntregaController.java:46` | PUT /API/V1/restaurantes/{id}/regioes/{id} | Direta |
| C48 | deletarRegiao() | `RegiaoEntregaController.java:54` | DELETE /API/V1/restaurantes/{id}/regioes/{id} | Direta |

---

## 5. Services

*Diagrama de origem: `docs/diagramas/Backend/pacotes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Métodos principais | Correspondência |
|----|--------------|---------------------|-------------------|----------------|
| S01 | MesaService | `services/MesaService.java` | criar, listarPorRestaurante, buscarPorId, atualizar, deletar, validarAcessoRestaurante | Direta |
| S02 | ComandaService | `services/ComandaService.java` | criar, listarPorMesa, buscarPorId, rateio, fechar, adicionarItem, atualizarItem, removerItem | Direta |
| S03 | ItemCompartilhadoService | `services/ItemCompartilhadoService.java` | adicionar, listar, atualizar, remover | Direta |
| S04 | PedidoService | `services/PedidoService.java` | criar, buscarPorId, listarPorUsuario, atualizarStatus | Direta |
| S05 | PixService | `services/PixService.java` | gerarQrCode, consultarStatus, processarWebhook | Direta |
| S06 | MercadoPagoPixClient | `services/MercadoPagoPixClient.java` | criarCobranca (chama API /v1/orders do MP) | Direta |
| S07 | PixClient (interface) | `services/PixClient.java` | interface criarCobranca | Direta |
| S08 | ProdutoService | `services/ProdutoService.java` | criar, buscarPorId, atualizar, deletar, listarPorRestaurante | Direta |
| S09 | RestauranteService | `services/RestauranteService.java` | criar, listarTodos, buscarPorId, atualizar, deletar | Direta |
| S10 | UsuarioService | `services/UsuarioService.java` | criar (com validação email), buscarPorId, listarTodos, atualizar, deletar | Direta |
| S11 | VinculoGarcomService | `services/VinculoGarcomService.java` | vincular, desvincular, listarGarcons | Direta |
| S12 | RegiaoEntregaService | `services/RegiaoEntregaService.java` | criar, listar, buscarPorId, atualizar, deletar | Direta |
| S13 | TokenService | `services/token/TokenService.java` | gerarToken, validarToken | Direta |
| S14 | UserDetailsServiceImpl | `services/impl/UserDetailsServiceImpl.java` | loadUserByUsername | Direta |

---

## 6. Repositories

*Diagrama de origem: `docs/diagramas/Backend/pacotes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Métodos principais | Correspondência |
|----|--------------|---------------------|-------------------|----------------|
| R01 | MesaRepository | `repositories/MesaRepository.java` | findAllByRestaurante_Id | Direta |
| R02 | ComandaRepository | `repositories/ComandaRepository.java` | findByMesaId, findByMesaIdOrderByDataAberturaDesc | Direta |
| R03 | ComandaItemRepository | `repositories/ComandaItemRepository.java` | save, findById, delete | Direta |
| R04 | ComandaRateioRepository | `repositories/ComandaRateioRepository.java` | findByComandaId, findByProdutoIdAndComandaMesa | Direta |
| R05 | ItemCompartilhadoRepository | `repositories/ItemCompartilhadoRepository.java` | findByMesaId, save, delete | Direta |
| R06 | ProdutoRepository | `repositories/ProdutoRepository.java` | findByRestauranteId | Direta |
| R07 | PedidoRepository | `repositories/PedidoRepository.java` | findByUsuarioId, findByRestauranteId | Direta |
| R08 | PagamentoRepository | `repositories/PagamentoRepository.java` | — | Direta |
| R09 | ItemPedidoRepository | `repositories/ItemPedidoRepository.java` | — | Direta |
| R10 | UsuarioRepository | `repositories/UsuarioRepository.java` | findByEmail | Direta |
| R11 | RestauranteRepository | `repositories/RestauranteRepository.java` | findByUsuarioId, findByCnpj | Direta |
| R12 | TransacaoPixRepository | `repositories/TransacaoPixRepository.java` | findByTxId, findByComandaId | Direta |
| R13 | RegiaoEntregaRepository | `repositories/RegiaoEntregaRepository.java` | findByRestauranteId | Direta |

---

## 7. Fluxos de Sequência

*Diagramas de origem: `docs/diagramas/Backend/sequencia-*.puml`*

### Fluxo 1: Abrir Mesa

| ID | Elemento UML | Artefato no Código | Método | Correspondência |
|----|--------------|---------------------|--------|----------------|
| SQ01 | Garçom → POST /API/V1/mesas | `MesaController.java:40` | criarMesa() | Direta |
| SQ02 | Controller → MesaService.criar() | `MesaService.java:37-53` | criar(requestDTO, usuarioLogado) | Direta |
| SQ03 | Service → valida restaurante do usuário | `MesaService.java:38-43` | getRestauranteVinculado() + valida | Direta |
| SQ04 | Service → MesaRepository.save() | `MesaService.java:53` | mesaRepository.save(mesa) | Direta |
| SQ05 | Service retorna MesaResponseDTO | `MesaService.java:103-130` | toResponseDTO(mesa) | Direta |

### Fluxo 2: Criar Comanda

| ID | Elemento UML | Artefato no Código | Método | Correspondência |
|----|--------------|---------------------|--------|----------------|
| SQ06 | Garçom → POST /mesas/{id}/comandas | `ComandaController.java:33` | criarComanda() | Direta |
| SQ07 | Controller → ComandaService.criar() | `ComandaService.java:53-70` | criar() | Direta |
| SQ08 | Service → valida mesa + busca restaurante | `ComandaService.java:55-60` | validarMesaRestaurante() | Direta |
| SQ09 | Para cada item → ProdutoRepository | `ComandaService.java:76-89` | produtoRepository.findById() | Direta |
| SQ10 | Service → cria ComandaItem + calcula total | `ComandaService.java:90-96` | new ComandaItem + cálculo | Direta |
| SQ11 | Service → ComandaRepository.save() | `ComandaService.java:97` | comandaRepository.save(comanda) | Direta |
| SQ12 | Service retorna ComandaResponseDTO | `ComandaService.java:290-315` | toResponseDTO(comanda) | Direta |

### Fluxo 3: Adicionar Item Compartilhado

| ID | Elemento UML | Artefato no Código | Método | Correspondência |
|----|--------------|---------------------|--------|----------------|
| SQ13 | Garçom → POST /mesas/{id}/compartilhados | `MesaController.java:83` | adicionarItemCompartilhado() | Direta |
| SQ14 | Controller → ItemCompartilhadoService | `ItemCompartilhadoService.java:28-52` | adicionar() | Direta |
| SQ15 | Service → valida mesa + busca produto | `ItemCompartilhadoService.java:31-42` | validaMesa + produtoRepository | Direta |
| SQ16 | Service → ItemCompartilhadoRepository.save() | `ItemCompartilhadoService.java:49` | save(item) | Direta |

### Fluxo 4: Fechar Conta com Rateio

| ID | Elemento UML | Artefato no Código | Método | Correspondência |
|----|--------------|---------------------|--------|----------------|
| SQ17 | Garçom → POST /comandas/{id}/rateio | `ComandaController.java:95` | rateio() | Direta |
| SQ18 | Service → valida comanda + busca mesa | `ComandaService.java:206-215` | rateio() + validações | Direta |
| SQ19 | Service → consulta mesa.itensCompartilhados | `ComandaService.java:218-224` | busca item no pool da mesa | Direta |
| SQ20 | Service → calcula saldo restante | `ComandaService.java:228-235` | sumRateios + valida saldo | Direta |
| SQ21 | Service → cria ComandaRateio + save | `ComandaService.java:237-243` | save(comanda) | Direta |
| SQ22 | Garçom → POST /comandas/{id}/fechar | `ComandaController.java:104` | fecharComanda() | Direta |
| SQ23 | Service → valida formaPagamento | `ComandaService.java:258-265` | fechar() + valida forma | Direta |
| SQ24 | Service → status = PAGA + dataFechamento | `ComandaService.java:267-275` | setStatus + setDataFechamento | Direta |
| SQ25 | Service → verifica se libera mesa | `ComandaService.java:277-280` | verificaUltimaComandaMesa() | Direta |

### Fluxo 5: PIX + Webhook

| ID | Elemento UML | Artefato no Código | Método | Correspondência |
|----|--------------|---------------------|--------|----------------|
| SQ26 | Garçom → POST /comandas/{id}/pix | `PixController.java:25` | gerarQrCode() | Direta |
| SQ27 | Controller → PixService | `PixService.java:65-105` | gerarQrCode() | Direta |
| SQ28 | Service → resolve token do restaurante | `PixService.java:75-90` | resolveAccessToken() | Direta |
| SQ29 | Service → MercadoPagoPixClient | `MercadoPagoPixClient.java:40-80` | criarCobranca() | Direta |
| SQ30 | Client → POST /v1/orders (MP API) | `MercadoPagoPixClient.java:60-75` | chamada HTTP ao MP | Direta |
| SQ31 | Service → salva TransacaoPix | `PixService.java:95-102` | transacaoPixRepository.save() | Direta |
| SQ32 | Service → retorna PixResponseDTO | `PixService.java:103` | toPixResponseDTO() | Direta |
| SQ33 | MP → POST /pix/webhook | `PixController.java:42` | receberWebhook() | Direta |
| SQ34 | Service → valida assinatura x-signature | `PixService.java:120-125` | validaAssinatura() | Direta |
| SQ35 | Service → localiza TransacaoPix | `PixService.java:128-132` | findByTxId() | Direta |
| SQ36 | Service → atualiza status PAGA | `PixService.java:135-145` | processarWebhook() | Direta |

---

## 8. Máquina de Estados

*Diagrama de origem: `docs/diagramas/Backend/estados-comanda.puml`*

| ID | Transição | Artefato no Código | Método | Correspondência |
|----|-----------|---------------------|--------|----------------|
| EST01 | Comanda ABERTA → PAGA | `ComandaService.java:267-275` | fechar() | Direta |
| EST02 | Comanda ABERTA → AGUARDANDO_PIX | `PixService.java:95-100` | gerarQrCode() | Direta |
| EST03 | Comanda AGUARDANDO_PIX → PAGA | `PixService.java:135-145` | processarWebhook() | Direta |
| EST04 | Mesa LIVRE → OCUPADA | `ComandaService.java:65-70` | criar() | Direta |
| EST05 | Mesa OCUPADA → LIVRE | `ComandaService.java:277-280` | fechar() (verifica última comanda) | Direta |

---

## 9. Configuração / Segurança

*Diagrama de origem: `docs/diagramas/Backend/pacotes-backend.puml`*

| ID | Elemento UML | Artefato no Código | Função | Correspondência |
|----|--------------|---------------------|--------|----------------|
| CF1 | SecurityConfig | `configs/SecurityConfig.java` | Configura rotas públicas, SecurityFilterChain, BCrypt, CORS | Direta |
| CF2 | SecurityFilter | `configs/SecurityFilter.java` | Filtro OncePerRequest: extrai JWT, valida, popula SecurityContext | Direta |
| CF3 | SecurityUtils | `configs/SecurityUtils.java` | Utilitário getUsuarioLogado() do SecurityContextHolder | Direta |
| CF4 | DataLoader | `configs/DataLoader.java` | Seeds banco H2: usuários, restaurante, produtos, mesas, comandas, itens | Direta |
| CF5 | MercadoPagoConfigSetup | `configs/MercadoPagoConfigSetup.java` | Carrega accessToken + webhookSecret de application.properties/secrets | Direta |

---

## Resumo Quantitativo

| Categoria | Total | Direta | Parcial | Indireta | Ausente | % Rastreado |
|-----------|-------|--------|---------|----------|---------|-------------|
| Classes (Entidades) | 13 | 13 | 0 | 0 | 0 | 100% |
| DTOs | 12 | 12 | 0 | 0 | 0 | 100% |
| Enums | 10 | 10 | 0 | 0 | 0 | 100% |
| Controllers (Endpoints) | 48 | 48 | 0 | 0 | 0 | 100% |
| Services | 14 | 14 | 0 | 0 | 0 | 100% |
| Repositories | 13 | 13 | 0 | 0 | 0 | 100% |
| Fluxos de Sequência | 36 | 36 | 0 | 0 | 0 | 100% |
| Máquina de Estados | 5 | 5 | 0 | 0 | 0 | 100% |
| Configuração / Segurança | 5 | 5 | 0 | 0 | 0 | 100% |
| **Total Geral** | **156** | **156** | **0** | **0** | **0** | **100%** |
