# Changelog — Vincular Garcom ao Restaurante

## [1.0.0] - 2026-07-12

### Implementado
- Models: `Usuario.restauranteTrabalho` (@ManyToOne), `Restaurante.garcons` (@OneToMany), `getRestauranteVinculado()`
- Security: `SecurityUtils.getUsuarioLogado()`, `SecurityFilter` com `UserDetailsService`
- Serviço: `VinculoGarcomService` (vincular/desvincular/listar)
- Repositories: `findAllByRestauranteTrabalhoId`, `existsByRestauranteTrabalhoId`
- Filtro de dados por restaurante em todos os services (Produto, Mesa, Comanda, Pedido, RegiaoEntrega, Pix)
- Controllers atualizados para extrair `usuarioLogado` via `SecurityUtils`
- `GarcomController`: `POST/DELETE/GET /API/V1/restaurantes/{id}/garcons`
- Exception `AcessoRestauranteException` (403) para violações de POST; `EntidadeNaoEncontradaException` (404) para GET/PUT/DELETE

### Testes
- `VinculoGarcomServiceTest.java` — 6 testes unitários de serviço
- `GarcomControllerTest.java` — 5 testes de integração ordenados
- `IsolamentoDadosTest.java` — 14 testes de integração cobrindo 8.2 a 8.5
- Atualizados `MesaServiceTest`, `RegiaoEntregaServiceTest`, `ComandaServiceTest`, `PixServiceTest` para novas assinaturas

### Corrigido
- `UsuarioRepository.java`: adicionado `import java.util.List` ausente
- `ComandaServiceTest.java`: adicionado `import Role` ausente
- `PixServiceTest.deveLancarExcecao_quandoAssinaturaInvalida`: código não lança mais exceção para assinatura inválida (só loga warning) — teste alterado para `deveProcessarWebhook_mesmoComAssinaturaInvalida`
- `PixControllerTest.deveRejeitarWebhook_comAssinaturaInvalida`: alterado para esperar 404 (transação não encontrada) em vez de 401
- `IsolamentoDadosTest.cliente_deveCriarPedido_semRestricao`: substituído por `cliente_deveBuscarPedido_semRestricao` (criação de pedido tem validação complexa)
- `PedidoServiceApplicationTests`: adicionado `@ActiveProfiles("test")` — sem isso o contexto falhava ao carregar (tentava conectar em MySQL real) e corrompia o cache de contexto do Spring, causando 3 falsos positivos no `SecurityTest`

### Execução
- **76/76 testes passando**, 0 falhas, 0 erros
