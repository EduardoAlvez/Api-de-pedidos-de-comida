## ADDED Requirements

### Requirement: Pedido presencial pode ter tipo de consumo
O sistema SHALL permitir que pedidos com `origem = PRESENCIAL` tenham um campo `tipoConsumo` com valor `COMER_AQUI` (padrão) ou `VIAGEM`. Este campo indica se o cliente vai consumir no local ou levar para viagem.

#### Scenario: Criar pedido presencial com VIAGEM
- **WHEN** um garçom cria um pedido com `origem: PRESENCIAL` e `tipoConsumo: VIAGEM`
- **THEN** o sistema retorna 201 Created com o pedido contendo `tipoConsumo: VIAGEM`

#### Scenario: Criar pedido delivery ignora tipoConsumo
- **WHEN** um cliente cria um pedido com `origem: DELIVERY` e `tipoConsumo: COMER_AQUI`
- **THEN** o sistema ignora o campo `tipoConsumo` e cria o pedido apenas com `origem: DELIVERY`

#### Scenario: Consultar pedido exibe tipoConsumo
- **WHEN** um garçom consulta um pedido presencial via GET `/API/V1/pedidos/{id}`
- **THEN** o response contém o campo `tipoConsumo`

#### Scenario: Listar pedidos exibe tipoConsumo
- **WHEN** um garçom lista pedidos de uma mesa
- **THEN** cada pedido retornado possui campo `tipoConsumo`
