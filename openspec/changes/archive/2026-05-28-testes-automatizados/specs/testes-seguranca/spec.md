## ADDED Requirements

### Requirement: Restrição de acesso por role em endpoints de comanda
O sistema DEVE garantir que cada endpoint de comanda respeite as roles corretas.

#### Scenario: Criar comanda como GARCOM deve retornar 201
- **DADO** um token com role GARCOM
- **QUANDO** enviar POST para `/API/V1/mesas/{id}/comandas`
- **ENTÃO** o status DEVE ser 201

#### Scenario: Criar comanda como CLIENTE deve retornar 403
- **DADO** um token com role CLIENTE
- **QUANDO** enviar POST para `/API/V1/mesas/{id}/comandas`
- **ENTÃO** o status DEVE ser 403

#### Scenario: Criar comanda sem token deve retornar 401
- **DADO** nenhum token
- **QUANDO** enviar POST para `/API/V1/mesas/{id}/comandas`
- **ENTÃO** o status DEVE ser 401

#### Scenario: Rateio como GARCOM deve retornar 200
- **DADO** um token com role GARCOM
- **QUANDO** enviar POST para `/API/V1/comandas/{id}/rateio`
- **ENTÃO** o status DEVE ser 200

#### Scenario: Rateio como CLIENTE deve retornar 403
- **DADO** um token com role CLIENTE
- **QUANDO** enviar POST para `/API/V1/comandas/{id}/rateio`
- **ENTÃO** o status DEVE ser 403

#### Scenario: Fechar comanda como GARCOM deve retornar 200
- **DADO** um token com role GARCOM
- **QUANDO** enviar POST para `/API/V1/comandas/{id}/fechar`
- **ENTÃO** o status DEVE ser 200

#### Scenario: Listar comandas como CLIENTE deve retornar 403
- **DADO** um token com role CLIENTE
- **QUANDO** enviar GET para `/API/V1/comandas`
- **ENTÃO** o status DEVE ser 403

### Requirement: Restrição de acesso por role em endpoints de mesa
O sistema DEVE garantir que endpoints de mesa respeitem as roles corretas.

#### Scenario: Criar mesa como GARCOM deve retornar 201
- **DADO** um token GARCOM
- **QUANDO** enviar POST para `/API/V1/mesas`
- **ENTÃO** o status DEVE ser 201

#### Scenario: Criar mesa como CLIENTE deve retornar 403
- **DADO** um token CLIENTE
- **QUANDO** enviar POST para `/API/V1/mesas`
- **ENTÃO** o status DEVE ser 403

### Requirement: Restrição de acesso em endpoints de pedido delivery
O sistema DEVE garantir que endpoints de delivery sejam acessíveis por CLIENTE.

#### Scenario: Criar pedido como CLIENTE deve retornar 201
- **DADO** um token CLIENTE
- **QUANDO** enviar POST para `/API/V1/pedidos`
- **ENTÃO** o status DEVE ser 201

### Requirement: Token inválido/expirado
O sistema DEVE rejeitar requisições com token inválido ou expirado.

#### Scenario: Token inválido deve retornar 401
- **DADO** um token JWT inválido (string aleatória)
- **QUANDO** enviar requisição para qualquer endpoint protegido
- **ENTÃO** o status DEVE ser 401

#### Scenario: Token expirado deve retornar 401
- **DADO** um token JWT expirado
- **QUANDO** enviar requisição para qualquer endpoint protegido
- **ENTÃO** o status DEVE ser 401
