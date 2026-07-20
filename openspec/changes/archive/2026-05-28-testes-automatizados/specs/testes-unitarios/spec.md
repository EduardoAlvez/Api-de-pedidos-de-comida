## ADDED Requirements

### Requirement: Testes unitários de ComandaService
O sistema DEVE ter testes unitários para `ComandaService` cobrindo criação, rateio e fechamento de comandas.

#### Scenario: Criar comanda em mesa LIVRE deve marcar mesa como OCUPADA
- **DADO** uma mesa com status LIVRE
- **QUANDO** uma comanda for criada nessa mesa
- **ENTÃO** a mesa DEVE ter status alterado para OCUPADA

#### Scenario: Criar comanda em mesa já OCUPADA deve manter OCUPADA
- **DADO** uma mesa com status OCUPADA
- **QUANDO** uma nova comanda for criada nessa mesa
- **ENTÃO** a mesa DEVE permanecer com status OCUPADA

#### Scenario: Rateio de item compartilhado deve validar saldo pendente
- **DADO** um item compartilhado com valor total de R\$ 12,00
- **E** um rateio de R\$ 5,00 já foi pago
- **QUANDO** um novo rateio de R\$ 10,00 for solicitado
- **ENTÃO** o sistema DEVE rejeitar com erro "valor excede saldo pendente"

#### Scenario: Rateio com valor exato do saldo deve ser aceito
- **DADO** um item compartilhado com saldo pendente de R\$ 7,00
- **QUANDO** um rateio de R\$ 7,00 for solicitado
- **ENTÃO** o rateio DEVE ser aceito

#### Scenario: Fechar comanda deve alterar status para PAGA
- **DADO** uma comanda com status ABERTA
- **QUANDO** a comanda for fechada
- **ENTÃO** o status DEVE ser PAGA
- **E** a data de fechamento DEVE ser preenchida

#### Scenario: Fechar comanda já PAGA deve lançar erro
- **DADO** uma comanda com status PAGA
- **QUANDO** tentar fechar novamente
- **ENTÃO** o sistema DEVE lançar exceção "Comanda já está paga"

#### Scenario: Fechar última comanda ABERTA deve liberar mesa
- **DADO** uma mesa com apenas uma comanda ABERTA
- **QUANDO** essa comanda for fechada
- **ENTÃO** a mesa DEVE ter status alterado para LIVRE

#### Scenario: Fechar comanda quando ainda há outras ABERTAS não deve liberar mesa
- **DADO** uma mesa com duas comandas ABERTAS
- **QUANDO** apenas uma delas for fechada
- **ENTÃO** a mesa DEVE permanecer OCUPADA

### Requirement: Testes unitários de MesaService
O sistema DEVE ter testes unitários para `MesaService` cobrindo CRUD e validações.

#### Scenario: Criar mesa com dados válidos
- **DADO** dados válidos de mesa (nomeCliente, restauranteId)
- **QUANDO** a mesa for criada
- **ENTÃO** o status DEVE ser LIVRE

#### Scenario: Buscar mesa por ID existente
- **DADO** uma mesa existente
- **QUANDO** buscar por ID
- **ENTÃO** os dados da mesa DEVEM ser retornados

#### Scenario: Buscar mesa por ID inexistente
- **DADO** um ID de mesa que não existe
- **QUANDO** buscar por ID
- **ENTÃO** o sistema DEVE lançar exceção

### Requirement: Testes unitários de RegiaoEntregaService
O sistema DEVE ter testes unitários para `RegiaoEntregaService` cobrindo cálculo de frete dinâmico.

#### Scenario: Calcular frete para região existente do restaurante
- **DADO** um restaurante com região de entrega "Centro" com frete R\$ 8,00
- **QUANDO** calcular frete para o restaurante na região "Centro"
- **ENTÃO** o valor DEVE ser R\$ 8,00

#### Scenario: Calcular frete para região que não pertence ao restaurante
- **DADO** um restaurante que não atende a região "Zona Sul"
- **QUANDO** calcular frete para essa região
- **ENTÃO** o sistema DEVE lançar exceção

### Requirement: Testes unitários de PedidoService
O sistema DEVE ter testes unitários para `PedidoService` cobrindo criação de pedidos delivery.

#### Scenario: Criar pedido delivery com dados válidos
- **DADO** dados válidos de pedido (restaurante, usuario, itens, endereco, regiao)
- **QUANDO** o pedido for criado
- **ENTÃO** o status DEVE ser AGUARDANDO_CONFIRMACAO
- **E** o frete DEVE ser calculado conforme a região
