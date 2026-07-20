## ADDED Requirements

### Requirement: Garcom ve apenas produtos do seu restaurante

O endpoint GET /API/V1/produtos/restaurante/{restauranteId} DEVE validar que o GARCOM ou DONO logado so pode acessar produtos do restaurante ao qual esta vinculado.

#### Scenario: Garcom acessa produtos do seu restaurante
- **WHEN** garcom vinculado ao restaurante X faz GET /API/V1/produtos/restaurante/{id do X}
- **THEN** retorna 200 OK com produtos daquele restaurante

#### Scenario: Garcom tenta acessar produtos de outro restaurante
- **WHEN** garcom vinculado ao restaurante X faz GET /API/V1/produtos/restaurante/{id de Y}
- **THEN** retorna 403 Forbidden

#### Scenario: CLIENTE acessa produtos de qualquer restaurante
- **WHEN** usuario CLIENTE faz GET /API/V1/produtos/restaurante/{qualquer id}
- **THEN** retorna 200 OK (CLIENTE nao tem restricao de restaurante)

### Requirement: Garcom ve apenas mesas do seu restaurante

O endpoint GET /API/V1/mesas com parametro ?restauranteId= DEVE filtrar conforme o vinculo do usuario logado.

#### Scenario: Garcom lista mesas do seu restaurante
- **WHEN** garcom logado faz GET /API/V1/mesas
- **THEN** retorna apenas mesas do restaurante ao qual o garcom esta vinculado

#### Scenario: Garcom tenta criar mesa em outro restaurante
- **WHEN** garcom faz POST /API/V1/mesas com restauranteId de outro estabelecimento
- **THEN** retorna 403 Forbidden

### Requirement: Garcom ve apenas comandas do seu restaurante

O endpoint GET /API/V1/comandas DEVE retornar apenas comandas das mesas que pertencem ao restaurante do usuario logado.

#### Scenario: Garcom lista comandas do seu restaurante
- **WHEN** garcom logado faz GET /API/V1/comandas
- **THEN** retorna apenas comandas de mesas do seu restaurante

#### Scenario: Garcom tenta buscar comanda de outro restaurante
- **WHEN** garcom logado faz GET /API/V1/comandas/{id de comanda de outro restaurante}
- **THEN** retorna 404 Not Found (nao revela existencia)

### Requirement: Garcom ve apenas pedidos do seu restaurante

O endpoint GET /API/V1/pedidos/usuario/{usuarioId} DEVE retornar apenas pedidos do restaurante do usuario logado quando o usuario logado for GARCOM ou DONO.

#### Scenario: Garcom lista pedidos do seu restaurante
- **WHEN** garcom logado faz GET /API/V1/pedidos/usuario/{usuarioId}
- **THEN** retorna apenas pedidos associados ao restaurante do garcom

### Requirement: Garcom so abre comanda em mesa do seu restaurante

O endpoint POST /API/V1/mesas/{mesaId}/comandas DEVE validar que a mesa pertence ao restaurante do usuario logado.

#### Scenario: Garcom abre comanda em mesa do seu restaurante
- **WHEN** garcom faz POST /API/V1/mesas/{mesaId}/comandas onde mesa pertence ao seu restaurante
- **THEN** retorna 201 Created com comanda criada

#### Scenario: Garcom tenta abrir comanda em mesa de outro restaurante
- **WHEN** garcom faz POST /API/V1/mesas/{mesaId}/comandas onde mesa pertence a outro restaurante
- **THEN** retorna 403 Forbidden

### Requirement: Garcom sem restaurante vinculado nao acessa dados restritos

Um usuario com role GARCOM que nao possui restauranteTrabalho definido DEVE receber erro ao tentar acessar endpoints que dependem de vinculo.

#### Scenario: Garcom sem vinculo tenta listar comandas
- **WHEN** garcom sem restauranteTrabalho faz GET /API/V1/comandas
- **THEN** retorna 400 Bad Request com mensagem "Usuario nao vinculado a nenhum restaurante"

### Requirement: DONO_RESTAURANTE respeita o mesmo isolamento

O DONO_RESTAURANTE DEVE ter o mesmo comportamento de isolamento que o GARCOM: acessa apenas dados do seu proprio restaurante.

#### Scenario: Dono lista apenas seus proprios produtos
- **WHEN** dono logado faz GET /API/V1/produtos/restaurante/{id do seu restaurante}
- **THEN** retorna 200 OK com produtos

#### Scenario: Dono tenta acessar dados de outro restaurante
- **WHEN** dono do restaurante X faz GET /API/V1/mesas?restauranteId={id de Y}
- **THEN** retorna 403 Forbidden

### Requirement: CLIENTE mantem acesso irrestrito a lista de restaurantes

O endpoint GET /API/V1/restaurantes DEVE continuar retornando todos os restaurantes para qualquer role.

#### Scenario: CLIENTE lista todos os restaurantes
- **WHEN** usuario CLIENTE faz GET /API/V1/restaurantes
- **THEN** retorna 200 OK com todos os restaurantes

#### Scenario: GARCOM lista todos os restaurantes
- **WHEN** usuario GARCOM faz GET /API/V1/restaurantes
- **THEN** retorna 200 OK com todos os restaurantes (necessario para selecionar/se identificar)
