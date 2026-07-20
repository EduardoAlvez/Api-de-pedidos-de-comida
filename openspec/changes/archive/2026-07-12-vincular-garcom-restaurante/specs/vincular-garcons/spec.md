## ADDED Requirements

### Requirement: Dono vincular garcom ao restaurante

O sistema DEVE permitir que um usuario com role DONO_RESTAURANTE vincule usuarios com role GARCOM ao seu restaurante. O vinculo deve ser validado: o usuario alvo DEVE existir, DEVE ter role GARCOM, e NAO DEVE estar ja vinculado a outro restaurante. O dono logado SO DEVE poder vincular garcons ao seu proprio restaurante.

#### Scenario: Dono vincula garcom com sucesso
- **WHEN** dono logado faz POST /API/V1/restaurantes/{restauranteId}/garcons com body {"usuarioId": 2}
- **THEN** retorna 200 OK e o garcom passa a ter restauranteTrabalho = restaurante do dono

#### Scenario: Dono tenta vincular garcom a restaurante que nao e seu
- **WHEN** dono do restaurante 1 faz POST /API/V1/restaurantes/2/garcons
- **THEN** retorna 403 Forbidden

#### Scenario: Dono tenta vincular usuario com role CLIENTE
- **WHEN** dono faz POST /API/V1/restaurantes/{id}/garcons com usuarioId de um CLIENTE
- **THEN** retorna 422 Unprocessable Entity com erro "Usuario deve ter role GARCOM"

#### Scenario: Dono tenta vincular garcom ja vinculado a outro restaurante
- **WHEN** dono faz POST /API/V1/restaurantes/{id}/garcons com usuarioId de um GARCOM ja vinculado
- **THEN** retorna 422 Unprocessable Entity com erro "Garcom ja vinculado a outro restaurante"

### Requirement: Dono desvincular garcom do restaurante

O sistema DEVE permitir que um DONO_RESTAURANTE remova o vinculo de um GARCOM ao seu restaurante.

#### Scenario: Dono desvincula garcom com sucesso
- **WHEN** dono logado faz DELETE /API/V1/restaurantes/{id}/garcons/{usuarioId}
- **THEN** retorna 204 No Content e o garcom fica com restauranteTrabalho = null

#### Scenario: Dono tenta desvincular garcom de outro restaurante
- **WHEN** dono do restaurante 1 faz DELETE /API/V1/restaurantes/2/garcons/{usuarioId}
- **THEN** retorna 403 Forbidden

### Requirement: DONO pode listar garcons do seu restaurante

O sistema DEVE listar os usuarios com role GARCOM vinculados ao restaurante do dono logado.

#### Scenario: Dono lista garcons do seu restaurante
- **WHEN** dono logado faz GET /API/V1/restaurantes/{id}/garcons
- **THEN** retorna 200 OK com lista de usuarios (id, nome, email) com role GARCOM vinculados ao restaurante
