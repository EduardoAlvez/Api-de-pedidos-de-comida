## 1. Modelos e Entidades

- [x] 1.1 Adicionar campo `restauranteTrabalho` (`@ManyToOne`) em `Usuario.java`
- [x] 1.2 Adicionar campo `garcons` (`@OneToMany`) em `Restaurante.java`
- [x] 1.3 Criar metodo `getRestauranteVinculado()` em `Usuario.java`

## 2. Utilitario de Seguranca

- [x] 2.1 Criar `SecurityUtils.java` com metodo `getUsuarioLogado()` que extrai `Usuario` do `SecurityContextHolder`

## 3. Repositories

- [x] 3.1 Adicionar `findByRestauranteId()` em `MesaRepository` (ja existia)
- [x] 3.2 Adicionar `findByRestauranteId()` em `PedidoRepository`
- [x] 3.3 Adicionar `findByRestauranteId()` em `RegiaoEntregaRepository` (ja existia)
- [x] 3.4 Adicionar `existsByRestauranteTrabalhoId()` em `UsuarioRepository`

## 4. Validacao de Vinculo de Garcom

- [x] 4.1 Criar servico `VinculoGarcomService` com metodos para vincular, desvincular e listar garcons
- [x] 4.2 Validar que usuario alvo tem role GARCOM e nao esta vinculado a outro restaurante
- [x] 4.3 Validar que apenas DONO do restaurante pode gerenciar vinculos

## 5. Filtro de Dados por Restaurante nos Services Existentes

- [x] 5.1 `ProdutoService.listarPorRestaurante()` — validar que usuario logado tem acesso ao restaurante
- [x] 5.2 `MesaService.listar()` — filtrar pelo restaurante do usuario logado
- [x] 5.3 `MesaService.criar()` — validar que mesa criada pertence ao restaurante do usuario
- [x] 5.4 `ComandaService.listar()` — filtrar comandas apenas do restaurante do usuario logado
- [x] 5.5 `ComandaService.abrirComanda()` — validar que mesa pertence ao restaurante do usuario
- [x] 5.6 `ComandaService.buscarPorId()` — retornar 404 se comanda nao pertence ao restaurante do usuario
- [x] 5.7 `PedidoService.listarPorUsuario()` — filtrar pelo restaurante quando usuario for GARCOM/DONO
- [x] 5.8 `RegiaoEntregaService` — filtrar pelo restaurante do usuario logado
- [x] 5.9 `PixService.gerarQrCode()` — validar que comanda pertence ao restaurante do usuario logado

## 6. Controllers e Endpoints

- [x] 6.1 Criar `GarcomController` com endpoints `POST/DELETE/GET /API/V1/restaurantes/{id}/garcons`
- [x] 6.2 Atualizar `MesaController` para extrair usuario logado e passar aos services
- [x] 6.3 Atualizar `ComandaController` para extrair usuario logado e passar aos services
- [x] 6.4 Atualizar `ProdutoController` para extrair usuario logado e passar ao service
- [x] 6.5 Atualizar `PedidoController` para extrair usuario logado e passar ao service
- [x] 6.6 Atualizar `RegiaoEntregaController` para extrair usuario logado e passar ao service

## 7. Tratamento de Erros

- [x] 7.1 Adicionar `ValidacaoNegocioException` para "Usuario nao vinculado a nenhum restaurante"
- [x] 7.2 Garantir que violacao de acesso retorne 403 sem revelar existencia de dados

## 8. Testes

- [x] 8.1 Testar vinculo de garcom por DONO (sucesso e falhas) — `VinculoGarcomServiceTest.java` (7 testes unitários)
- [x] 8.2 Testar isolamento: garcom acessa apenas dados do seu restaurante — `IsolamentoDadosTest.java` (5 testes)
- [x] 8.3 Testar isolamento: dono acessa apenas dados do seu restaurante — `IsolamentoDadosTest.java` (3 testes)
- [x] 8.4 Testar que CLIENTE continua acessando dados sem restricao — `IsolamentoDadosTest.java` (2 testes)
- [x] 8.5 Testar garcom sem vinculo tentando operacoes — `IsolamentoDadosTest.java` (3 testes)
