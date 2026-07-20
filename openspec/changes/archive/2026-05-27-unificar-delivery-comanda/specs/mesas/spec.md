## ADDED Requirements

### Requirement: Gerenciar mesas do restaurante
O sistema SHALL permitir que garçons e donos de restaurante gerenciem mesas. Cada mesa pertence a um restaurante, é identificada pelo nome do cliente principal e possui um status (LIVRE ou OCUPADA).

#### Scenario: Garçom abre uma mesa
- **WHEN** um garçom autenticado envia POST para `/API/V1/mesas` com `restauranteId` e `nomeCliente` válidos
- **THEN** o sistema retorna 201 Created com status `LIVRE`

#### Scenario: Garçom lista mesas de um restaurante
- **WHEN** um garçom autenticado envia GET para `/API/V1/mesas?restauranteId={id}`
- **THEN** o sistema retorna 200 OK com a lista de mesas

#### Scenario: Buscar mesa por ID
- **WHEN** um usuário autenticado envia GET para `/API/V1/mesas/{id}`
- **THEN** o sistema retorna 200 OK com os dados da mesa

#### Scenario: Mesa não encontrada
- **WHEN** um usuário envia GET para `/API/V1/mesas/{id}` e o ID não existe
- **THEN** o sistema retorna 404 Not Found

#### Scenario: Fechar mesa (liberar)
- **WHEN** um garçom autenticado envia DELETE para `/API/V1/mesas/{id}` e a mesa não tem comandas abertas
- **THEN** o sistema retorna 204 No Content e a mesa é liberada

#### Scenario: Fechar mesa com comandas abertas
- **WHEN** um garçom tenta fechar uma mesa que possui comandas com status ABERTA ou FECHADA
- **THEN** o sistema retorna 400 Bad Request com mensagem de erro

### Requirement: Autenticação e autorização de garçom
O sistema SHALL autenticar garçons via JWT, igual aos demais usuários. Garçons possuem role `GARCOM` e acessam apenas endpoints de mesa/comanda.

#### Scenario: Garçom faz login
- **WHEN** um garçom envia POST para `/login` com email e senha válidos
- **THEN** o sistema retorna 200 OK com token JWT contendo role `GARCOM`

#### Scenario: Garçom não pode acessar endpoints de delivery
- **WHEN** um garçom autenticado envia GET para `/API/V1/pedidos`
- **THEN** o sistema retorna 403 Forbidden (se houver restrição por role) ou 200 (se não houver — decidir em implementação)
