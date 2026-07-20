## ADDED Requirements

### Requirement: Produto pode ter preço de meia porção
O sistema SHALL permitir que um produto tenha um `precoMeia` opcional (nullable). Se preenchido, o produto oferece meia porção. Se null, o produto só é vendido como inteira.

#### Scenario: Criar produto com precoMeia
- **WHEN** um dono de restaurante envia POST para `/API/V1/produtos` com `precoMeia: 12.50`
- **THEN** o sistema retorna 201 Created e o produto possui `precoMeia` registrado

#### Scenario: Criar produto sem precoMeia
- **WHEN** um dono de restaurante envia POST para `/API/V1/produtos` sem `precoMeia`
- **THEN** o sistema retorna 201 Created e o `precoMeia` fica como null

#### Scenario: Atualizar produto adicionando precoMeia
- **WHEN** um dono de restaurante envia PUT para `/API/V1/produtos/{id}` com `precoMeia: 15.00`
- **THEN** o sistema retorna 200 OK e o produto passa a ter meia porção disponível

### Requirement: Item do pedido pode ter tamanho INTEIRA ou MEIA
O sistema SHALL permitir que cada item do pedido tenha um campo `tamanho` com valor `INTEIRA` (padrão) ou `MEIA`. O preço do item é calculado com base no tamanho e no preço do produto.

#### Scenario: Criar item do pedido com tamanho INTEIRA
- **WHEN** um garçom adiciona item ao pedido sem especificar `tamanho`
- **THEN** o sistema assume `INTEIRA` e usa `produto.preco` para calcular o subtotal

#### Scenario: Criar item do pedido com tamanho MEIA
- **WHEN** um garçom adiciona item ao pedido com `tamanho: MEIA`
- **THEN** o sistema usa `produto.precoMeia` para calcular o subtotal

#### Scenario: Item MEIA em produto sem precoMeia
- **WHEN** um garçom tenta adicionar item com `tamanho: MEIA` em um produto que não possui `precoMeia`
- **THEN** o sistema retorna 400 Bad Request com a mensagem "Produto não oferece meia porção"

#### Scenario: Listar item do pedido exibe tamanho
- **WHEN** um cliente consulta um pedido via GET `/API/V1/pedidos/{id}`
- **THEN** cada item retornado possui campo `tamanho` indicando INTEIRA ou MEIA

### Requirement: Cálculo do pedido considera tamanho do item
O sistema SHALL calcular o subtotal de cada item considerando seu tamanho: se `INTEIRA` usa `produto.preco * quantidade`; se `MEIA` usa `produto.precoMeia * quantidade`. O valor total do pedido é a soma de todos os subtotais.

#### Scenario: Pedido com itens mistos (inteira e meia)
- **WHEN** um pedido contém itens de tamanhos diferentes
- **THEN** o sistema calcula cada subtotal conforme o tamanho e soma corretamente

#### Scenario: Pedido com item meia sem precoMeia na atualização
- **WHEN** um produto tem `precoMeia` removido (atualizado para null) e existe pedido pendente com item MEIA desse produto
- **THEN** o sistema permite o pedido existente (validação só ocorre no momento da criação do item)
