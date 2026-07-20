## ADDED Requirements

### Requirement: Testes de integração de ComandaController
O sistema DEVE ter testes de integração para os endpoints de comanda.

#### Scenario: POST /API/V1/mesas/{mesaId}/comandas deve retornar 201 Created
- **DADO** um token de GARCOM válido
- **E** uma mesa existente
- **E** dados válidos de comanda (clienteNome + itens)
- **QUANDO** enviar POST para `/API/V1/mesas/{mesaId}/comandas`
- **ENTÃO** o status DEVE ser 201 Created
- **E** o corpo DEVE conter os dados da comanda criada

#### Scenario: POST /API/V1/mesas/{mesaId}/comandas com produto inexistente deve retornar 404
- **DADO** dados de comanda com produtoId que não existe
- **QUANDO** enviar POST
- **ENTÃO** o status DEVE ser 404

#### Scenario: GET /API/V1/comandas?mesaId={id} deve listar comandas da mesa
- **DADO** uma mesa com 2 comandas
- **QUANDO** enviar GET para `/API/V1/comandas?mesaId={id}`
- **ENTÃO** o status DEVE ser 200
- **E** o corpo DEVE ser um array com 2 comandas

#### Scenario: POST /API/V1/comandas/{id}/rateio deve retornar 200
- **DADO** uma comanda com item compartilhado
- **E** um rateio com valor válido
- **QUANDO** enviar POST para `/API/V1/comandas/{id}/rateio`
- **ENTÃO** o status DEVE ser 200
- **E** o rateio DEVE aparecer na lista de rateios da comanda

#### Scenario: POST /API/V1/comandas/{id}/rateio com valor excessivo deve retornar 422
- **DADO** uma comanda com item compartilhado de R\$ 12,00 já totalmente rateado
- **QUANDO** enviar POST com valor de R\$ 1,00
- **ENTÃO** o status DEVE ser 422

#### Scenario: POST /API/V1/comandas/{id}/fechar deve retornar 200
- **DADO** uma comanda ABERTA
- **QUANDO** enviar POST para `/API/V1/comandas/{id}/fechar`
- **ENTÃO** o status DEVE ser 200
- **E** o status da comanda DEVE ser PAGA

### Requirement: Testes de integração de MesaController
O sistema DEVE ter testes de integração para os endpoints de mesa.

#### Scenario: POST /API/V1/mesas deve criar mesa
- **DADO** dados válidos de mesa (nomeCliente, restauranteId)
- **QUANDO** enviar POST para `/API/V1/mesas`
- **ENTÃO** o status DEVE ser 201
- **E** o status da mesa DEVE ser LIVRE

#### Scenario: GET /API/V1/mesas?restauranteId={id} deve listar mesas
- **DADO** um restaurante com mesas cadastradas
- **QUANDO** enviar GET para `/API/V1/mesas?restauranteId={id}`
- **ENTÃO** o status DEVE ser 200

### Requirement: Testes de integração de PedidoController (Delivery)
O sistema DEVE ter testes de integração para endpoints de pedido delivery.

#### Scenario: POST /API/V1/pedidos deve criar pedido delivery
- **DADO** dados válidos de pedido delivery
- **QUANDO** enviar POST
- **ENTÃO** o status DEVE ser 201
- **E** o pedido DEVE ter status AGUARDANDO_CONFIRMACAO

#### Scenario: POST /API/V1/pedidos sem endereço deve retornar 422
- **DADO** dados de pedido sem enderecoDeEntrega
- **QUANDO** enviar POST
- **ENTÃO** o status DEVE ser 422
- **E** a resposta DEVE conter erro de validação

### Requirement: Testes de integração de RegiaoEntregaController
O sistema DEVE ter testes de integração para endpoints de região de entrega.

#### Scenario: CRUD de regiões de entrega
- **DADO** um restaurante existente
- **QUANDO** criar, listar, buscar e deletar regiões
- **ENTÃO** todos os endpoints DEVEM retornar status esperados

### Requirement: Testes de integração de AutenticacaoController
O sistema DEVE ter testes de integração para o fluxo de autenticação.

#### Scenario: Login com credenciais válidas deve retornar token
- **DADO** um usuário cadastrado
- **QUANDO** enviar POST para `/login` com email e senha corretos
- **ENTÃO** o status DEVE ser 200
- **E** a resposta DEVE conter um token JWT

#### Scenario: Login com senha inválida deve retornar 403
- **DADO** um usuário cadastrado
- **QUANDO** enviar POST para `/login` com senha incorreta
- **ENTÃO** o status DEVE ser 403
