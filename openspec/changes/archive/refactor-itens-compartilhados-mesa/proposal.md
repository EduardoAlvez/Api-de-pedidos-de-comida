## Why

Atualmente o flag `compartilhado` está no `ComandaItem`, mas itens de consumo compartilhado (ex: pizza, refrigerante) pertencem à mesa como um todo, não a uma comanda individual. Isso confunde o modelo de dados: um item "pertence" a uma pessoa mas na verdade é de todos. Além disso, não há uma forma clara de adicionar itens compartilhados independentemente das comandas.

## What Changes

- **Nova entidade `ItemCompartilhado`** ligada diretamente à `Mesa`, removendo o flag `compartilhado` de `ComandaItem`
- `Mesa` ganha um campo `nomeCliente` que identifica o **responsável pela mesa** (preenchido na criação)
- `Mesa` ganha uma lista `itensCompartilhados` inicializada vazia
- **BREAKING**: `ComandaItem` perde o campo `compartilhado` — itens de comanda passam a ser exclusivamente individuais
- **BREAKING**: `MesaRequestDTO` passa a exigir `nomeCliente` (responsável) em vez de opcional
- Novos endpoints para gerenciar itens compartilhados na mesa
- Rateio (`POST /API/V1/comandas/{id}/rateio`) passa a consultar o pool de `itensCompartilhados` da mesa em vez de filtrar `compartilhado=true`

### Fluxo final esperado

1. Garçom cria mesa informando o responsável (`nomeCliente`)
2. Garçom adiciona itens compartilhados à mesa via endpoint específico
3. Garçom cria comandas individuais para cada pessoa (itens pessoais)
4. Ao pagar, cada pessoa fecha sua comanda e rateia os compartilhados conforme desejar

## Capabilities

### New Capabilities
- `gerenciar-itens-compartilhados`: Adicionar, listar e remover itens compartilhados do pool da mesa

### Modified Capabilities
<!-- Nenhuma spec existente foi modificada, pois não há specs criadas ainda -->

## Impact

- **Modelos**: Nova entidade `ItemCompartilhado` + alteração em `Mesa` (adicionar lista) + remoção do campo `compartilhado` em `ComandaItem`
- **DTOs**: `MesaRequestDTO` (adicionar `nomeCliente`), novos DTOs para `ItemCompartilhado` (request/response)
- **Controllers**: Novo `ItemCompartilhadoController` (ou endpoints em `MesaController`)
- **Services**: Novo `ItemCompartilhadoService` + alteração em `ComandaService.rateio`
- **Repositories**: Novo `ItemCompartilhadoRepository`
- **Database**: Nova tabela `itens_compartilhados` + migração
- **Testes**: Atualizar testes existentes que usam `compartilhado` + novos testes para o CRUD de itens compartilhados
