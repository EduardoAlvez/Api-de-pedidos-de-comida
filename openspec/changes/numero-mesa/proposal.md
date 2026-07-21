## Why

Atualmente a mesa não possui um campo `numero`. O garçom digita um número no front-end que vira `nomeCliente`, misturando identificador visual com nome do cliente. O front-end precisa de um número de mesa distinto para exibição, independente do `id` técnico e do `nomeCliente`.

Issosepara do `id` (identificador técnico do banco) e do `nomeCliente` (nome do responsável pela mesa).

## What Changes

- **Adicionar** campo `numero` (Integer, nullable) na entidade `Mesa`
- **Adicionar** campo `numero` em `MesaRequestDTO` (opcional) e `MesaResponseDTO`
- **Adicionar** validação: número único por restaurante
- **Criar** endpoint para listar números disponíveis (opcional)
- **Atualizar** `MesaService` para validar unicidade

### Fluxo final esperado

1. Garçom abre mesa informando: número, nome do cliente
2. Backend valida que o número não está em uso no restaurante
3. Frontend exibe o número da mesa na lista
4. Garçom identifica a mesa pelo número na UI

## Impact

- **Modelos**: `Mesa.java` ganha campo `numero`
- **DTOs**: `MesaRequestDTO` ganha `numero` opcional; `MesaResponseDTO` ganha `numero`
- **Service**: `MesaService` valida unicidade do número por restaurante
- **Testes**: Atualizar testes de criação de mesa
