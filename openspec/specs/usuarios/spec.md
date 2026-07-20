## ADDED Requirements

### Requirement: Cadastro público de usuários
O sistema SHALL permitir que qualquer pessoa se cadastre como CLIENTE. O papel (tipo) é forçado para `CLIENTE` pelo backend — cadastro de `DONO_RESTAURANTE` e `GARCOM` segue fluxos específicos (vínculo por restaurante).

#### Scenario: Cadastro com sucesso
- **WHEN** uma pessoa envia POST para `/API/V1/usuarios` com `nome`, `email`, `telefone` e `senha` válidos
- **THEN** o sistema retorna 201 Created com tipo `CLIENTE`
- **THEN** o campo tipo nunca é aceito do payload (ignorado pelo service)

#### Scenario: Cadastro com email já existente
- **WHEN** uma pessoa tenta cadastrar com email já cadastrado
- **THEN** o sistema retorna 409 Conflict com mensagem "Este e-mail já está cadastrado."

#### Scenario: Cadastro tentando assumir papel diferente
- **WHEN** uma pessoa envia o campo `tipo` no payload de cadastro
- **THEN** o campo é ignorado e o usuário é criado como `CLIENTE`

### Requirement: Autoidentificação em operações de conta
O sistema SHALL exigir que o usuário autenticado seja o mesmo usuário sendo alterado ou deletado. Operações em contas de outros usuários são proibidas.

#### Scenario: Atualizar própria conta
- **WHEN** um usuário autenticado envia PUT para `/API/V1/usuarios/{id}` com seu próprio ID
- **THEN** o sistema atualiza os dados e retorna 200 OK
- **THEN** o campo `tipo` não pode ser alterado por este endpoint

#### Scenario: Atualizar conta de outro usuário (bloqueado)
- **WHEN** um usuário autenticado envia PUT para `/API/V1/usuarios/{id}` com ID de outro usuário
- **THEN** o sistema retorna 400 Bad Request com mensagem "Você só pode alterar sua própria conta."

#### Scenario: Deletar própria conta
- **WHEN** um usuário autenticado envia DELETE para `/API/V1/usuarios/{id}` com seu próprio ID
- **THEN** o sistema retorna 204 No Content e remove a conta

#### Scenario: Deletar conta de outro usuário (bloqueado)
- **WHEN** um usuário autenticado envia DELETE para `/API/V1/usuarios/{id}` com ID de outro usuário
- **THEN** o sistema retorna 400 Bad Request com mensagem "Você só pode deletar sua própria conta."

### Requirement: Restrição de acesso a listagem de usuários
O sistema SHALL não disponibilizar listagem pública de todos os usuários. O endpoint `GET /API/V1/usuarios` (sem ID) foi removido. Cada usuário consulta seus próprios dados via `GET /API/V1/usuarios/{id}`.

#### Scenario: Listar todos os usuários (removido)
- **WHEN** um usuário envia GET para `/API/V1/usuarios`
- **THEN** o sistema retorna 404 Not Found (endpoint removido)

#### Scenario: Buscar próprio usuário
- **WHEN** um usuário autenticado envia GET para `/API/V1/usuarios/{id}`
- **THEN** o sistema retorna 200 OK com os dados do usuário

### Requirement: Senha segura em todas as operações
O sistema SHALL tratar senha com BCrypt em todas as operações de criação e atualização. A senha nunca é exposta em respostas JSON.

#### Scenario: Criação com hash
- **WHEN** um usuário é criado via `POST /API/V1/usuarios`
- **THEN** a senha é hasheada com BCrypt antes de persistir

#### Scenario: Atualização com hash
- **WHEN** um usuário atualiza a senha via `PUT /API/V1/usuarios/{id}`
- **THEN** a nova senha é hasheada com BCrypt antes de persistir

#### Scenario: Atualização sem enviar nova senha
- **WHEN** um usuário envia PUT sem campo `senha` (ou vazio)
- **THEN** a senha existente é mantida (não sobrescrita com null)
