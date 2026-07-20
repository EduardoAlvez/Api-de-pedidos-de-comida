## ADDED Requirements

### Requirement: Dono cria garçom em um único passo
O sistema SHALL permitir que um dono de restaurante crie um novo garçom e o vincule ao seu restaurante em uma única operação. O papel (tipo) é forçado para `GARCOM` pelo backend.

#### Scenario: Criação com sucesso
- **WHEN** um dono autenticado (`DONO_RESTAURANTE`) envia POST para `/API/V1/restaurantes/{restauranteId}/garcons` com `nome`, `email`, `telefone` e `senha` válidos
- **THEN** o sistema cria o usuário com `Role.GARCOM`
- **THEN** o sistema vincula o garçom ao restaurante (seta `restauranteTrabalho`)
- **THEN** o sistema retorna 201 Created com os dados do garçom (incluindo ID e `tipo: GARCOM`)

#### Scenario: Não-dono tenta criar garçom
- **WHEN** um usuário autenticado que não é dono do restaurante envia POST para `/API/V1/restaurantes/{restauranteId}/garcons`
- **THEN** o sistema retorna 403 Forbidden com mensagem "Apenas donos de restaurante podem criar garcons."

#### Scenario: Email duplicado
- **WHEN** o dono tenta criar garçom com email já cadastrado
- **THEN** o sistema retorna 400 Bad Request com mensagem "Este e-mail já está cadastrado."

#### Scenario: Validação de campos
- **WHEN** o dono envia dados com campos obrigatórios faltando ou inválidos (nome < 3 caracteres, email inválido, telefone < 10 ou > 15 caracteres, senha < 6 caracteres)
- **THEN** o sistema retorna 400 Bad Request com mensagens de validação

### Requirement: Vínculo de garçom existente
O sistema SHALL permitir que um dono vincule um usuário `GARCOM` existente ao seu restaurante, caso o garçom ainda não esteja vinculado a outro restaurante.

#### Scenario: Vínculo com sucesso
- **WHEN** um dono envia POST para `/API/V1/restaurantes/{restauranteId}/garcons/vincular` com `{ "usuarioId": <id> }`
- **THEN** o sistema vincula o garçom ao restaurante e retorna 200 OK

#### Scenario: Usuário não é GARCOM
- **WHEN** o dono tenta vincular um usuário com papel diferente de `GARCOM`
- **THEN** o sistema retorna 400 Bad Request com mensagem "Usuario deve ter role GARCOM."

#### Scenario: Garçom já vinculado
- **WHEN** o dono tenta vincular um garçom que já está vinculado a outro restaurante
- **THEN** o sistema retorna 400 Bad Request com mensagem "Garcom ja vinculado a outro restaurante."

### Requirement: Desvincular garçom
O sistema SHALL permitir que um dono desvincule um garçom do seu restaurante.

#### Scenario: Desvincular com sucesso
- **WHEN** um dono envia DELETE para `/API/V1/restaurantes/{restauranteId}/garcons/{usuarioId}`
- **THEN** o sistema remove a vinculação (`restauranteTrabalho = null`) e retorna 204 No Content

#### Scenario: Garçom não está vinculado a este restaurante
- **WHEN** o dono tenta desvincular um garçom que não está vinculado ao restaurante
- **THEN** o sistema retorna 400 Bad Request com mensagem "Garcom nao esta vinculado a este restaurante."

### Requirement: Listar garçons do restaurante
O sistema SHALL permitir que um dono liste todos os garçons vinculados ao seu restaurante.

#### Scenario: Listar com sucesso
- **WHEN** um dono envia GET para `/API/V1/restaurantes/{restauranteId}/garcons`
- **THEN** o sistema retorna 200 OK com a lista de garçons (id, nome, email, tipo)

### Requirement: Restrição de acesso por papel
O sistema SHALL exigir que o usuário autenticado tenha papel `DONO_RESTAURANTE` para todas as operações de gerenciamento de garçons (criar, vincular, desvincular, listar).

#### Scenario: Operação sem autenticação
- **WHEN** um usuário não autenticado tenta acessar endpoints de garçons
- **THEN** o sistema retorna 401 Unauthorized
