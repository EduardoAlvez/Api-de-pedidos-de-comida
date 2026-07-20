## 1. Modelos e Entidades

- [x] 1.1 Criar entidade `ItemCompartilhado` (`models/ItemCompartilhado.java`) com campos: `id`, `produto` (ManyToOne), `quantidade`, `precoUnitario`, `observacao`, `mesa` (ManyToOne)
- [x] 1.2 Adicionar em `Mesa` o campo `@OneToMany List<ItemCompartilhado> itensCompartilhados` com cascade ALL
- [x] 1.3 Remover campo `compartilhado` de `ComandaItem`
- [x] 1.4 Criar `ItemCompartilhadoRepository`
- [x] 1.5 Adicionar campo `tamanho` (TamanhoPorcao) em `ItemCompartilhado`
- [x] 1.6 Adicionar campo `tamanho` (TamanhoPorcao) em `ComandaItem`

## 2. DTOs

- [x] 2.1 Criar `ItemCompartilhadoRequestDTO` (`produtoId`, `quantidade`, `observacao` opcional)
- [x] 2.2 Criar `ItemCompartilhadoResponseDTO` (`id`, `produtoId`, `nomeProduto`, `quantidade`, `precoUnitario`, `observacao`)
- [x] 2.3 Adicionar `List<ItemCompartilhadoResponseDTO> itensCompartilhados` em `MesaResponseDTO`
- [x] 2.4 Remover campo `compartilhado` de `ComandaItemRequestDTO` e `ComandaItemResponseDTO`
- [x] 2.5 Adicionar campo `tamanho` em `ComandaItemRequestDTO`, `ComandaItemResponseDTO`, `ItemCompartilhadoRequestDTO`, `ItemCompartilhadoResponseDTO`

## 3. Service e Controller

- [x] 3.1 Criar `ItemCompartilhadoService` com métodos: `adicionar(Long mesaId, ItemCompartilhadoRequestDTO, Usuario)`, `listar(Long mesaId, Usuario)`, `remover(Long mesaId, Long itemId, Usuario)`
- [x] 3.2 Adicionar endpoints em `MesaController`:
  - `POST /API/V1/mesas/{mesaId}/compartilhados` → 201
  - `GET /API/V1/mesas/{mesaId}/compartilhados` → 200
  - `PUT /API/V1/mesas/{mesaId}/compartilhados/{itemId}` → 200
  - `DELETE /API/V1/mesas/{mesaId}/compartilhados/{itemId}` → 204
- [x] 3.3 Atualizar `MesaService.toResponseDTO` para incluir `itensCompartilhados`
- [x] 3.4 Atualizar `ComandaService.rateio` para consultar `mesa.itensCompartilhados` em vez de filtrar `compartilhado=true`
- [x] 3.5 Adicionar lógica de `precoMeia` em `ItemCompartilhadoService.adicionar` e `atualizar`
- [x] 3.6 Adicionar lógica de `precoMeia` em `ComandaService.criar` e `adicionarItem`
- [x] 3.7 Limpar `ItemCompartilhado` ao fechar última comanda em `ComandaService.fechar`

## 4. DataLoader

- [x] 4.1 Atualizar `DataLoader` para não usar `compartilhado` nos itens de comanda
- [x] 4.2 Adicionar alguns `ItemCompartilhado` de exemplo na mesa criada

## 5. Testes

- [x] 5.1 Remover campo `compartilhado` dos JSONs em `ComandaControllerTest`, `IsolamentoDadosTest` e `SecurityTest`
- [x] 5.2 Atualizar teste de rateio em `ComandaControllerTest` para usar itens compartilhados da mesa em vez de flag
- [x] 5.3 Criar testes para o CRUD de itens compartilhados (`ItemCompartilhadoControllerTest` ou adicionar em `MesaControllerTest`)
- [x] 5.4 Rodar todos os testes e verificar que passam (76/76)
- [x] 5.5 Corrigir `GarcomControllerTest` (URL `/garcons` → `/garcons/vincular`)
- [x] 5.6 Adicionar mock de `ItemCompartilhadoRepository` em `ComandaServiceTest`

## 6. Verificação

- [x] 6.1 Rodar `mvnw compile` para verificar compilação
- [x] 6.2 Rodar `mvnw test` para verificar todos os testes
- [ ] 6.3 Rodar `mvnw spring-boot:run` para verificar startup
