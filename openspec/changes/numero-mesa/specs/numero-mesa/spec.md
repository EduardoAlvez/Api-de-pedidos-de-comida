## ADDED Requirements

### Requirement: Mesa tem número opcional único por restaurante
O sistema SHALL permitir que cada mesa tenha um número inteiro opcional, único dentro do mesmo restaurante.

#### Scenario: Criar mesa com número
- **WHEN** garçom envia POST `/API/V1/mesas` com `numero: 5` e `nomeCliente: "João"`
- **THEN** sistema retorna 201 Created com a mesa contendo `numero: 5`

#### Scenario: Criar mesa sem número
- **WHEN** garçom envia POST `/API/V1/mesas` sem `numero`
- **THEN** sistema retorna 201 Created com `numero: null`

#### Scenario: Criar mesa com número duplicado no mesmo restaurante
- **WHEN** garçom tenta criar mesa com `numero: 5` em restaurante que já tem mesa com número 5
- **THEN** sistema retorna 400 Bad Request com mensagem "Número de mesa já em uso neste restaurante"

#### Scenario: Mesmo número em restaurantes diferentes
- **WHEN** restaurante 1 tem mesa 5 e restaurante 2 tenta criar mesa 5
- **THEN** sistema permite (número é único apenas dentro do mesmo restaurante)

### Requirement: Listar mesas exibe número
O sistema SHALL retornar o campo `numero` ao listar/buscar mesas.
- `MesaResponseDTO` ganha campo `numero`
- `GET /API/V1/mesas` retorna `numero` em cada mesa
- `GET /API/V1/mesas/{id}` retorna `numero`
