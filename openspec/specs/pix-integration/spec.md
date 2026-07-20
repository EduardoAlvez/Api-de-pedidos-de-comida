## ADDED Requirements

### Requirement: Gerar QR Code Pix
O sistema DEVE gerar um QR Code Pix dinâmico via API do Mercado Pago ao solicitar pagamento Pix para uma comanda.

#### Scenario: Geração de QR Code com sucesso (comanda sem rateio)
- **WHEN** o garçom solicita pagamento Pix para uma comanda ABERTA sem rateios
- **THEN** o sistema cria uma transação Pix no Mercado Pago com valor = comanda.valorTotal
- **AND** retorna a imagem do QR Code em Base64 e o payload Pix Copia e Cola
- **AND** altera o status da comanda para AGUARDANDO_PIX

#### Scenario: Geração de QR Code com rateio já realizado (pagamento parcial)
- **WHEN** o garçom solicita pagamento Pix para uma comanda ABERTA que já possui rateios pagos
- **THEN** o sistema calcula o saldo restante: valorTotal - totalRateios
- **AND** cria uma transação Pix no Mercado Pago com o saldo restante
- **AND** retorna o QR Code com o valor parcial
- **AND** altera o status da comanda para AGUARDANDO_PIX

#### Scenario: Geração de QR Code com valor total já rateado
- **WHEN** o garçom solicita pagamento Pix para uma comanda onde o total dos rateios já iguala o valorTotal
- **THEN** o sistema retorna erro 400 "Comanda já foi totalmente paga via rateio"

#### Scenario: Comanda já está PAGA
- **WHEN** o garçom solicita pagamento Pix para uma comanda com status PAGA
- **THEN** o sistema retorna erro 400 "Comanda já está paga"

#### Scenario: Comanda já está AGUARDANDO_PIX
- **WHEN** o garçom solicita pagamento Pix para uma comanda com status AGUARDANDO_PIX
- **THEN** o sistema retorna o QR Code já gerado anteriormente (idempotente)

### Requirement: Receber webhook de confirmação do Mercado Pago
O sistema DEVE expor um endpoint para receber notificações do Mercado Pago quando um Pix for pago.

#### Scenario: Confirmação de pagamento válida
- **WHEN** o Mercado Pago envia webhook com txId e status "approved"
- **AND** a assinatura HMAC é válida
- **THEN** o sistema localiza a TransacaoPix pelo txId
- **AND** atualiza o status para CONFIRMADO
- **AND** altera o status da comanda para PAGA
- **AND** registra data de confirmação

#### Scenario: Assinatura inválida
- **WHEN** o Mercado Pago envia webhook com assinatura HMAC inválida
- **THEN** o sistema retorna erro 401 "Assinatura inválida"
- **AND** não altera nenhum status

#### Scenario: txId não encontrado
- **WHEN** o Mercado Pago envia webhook com txId inexistente
- **THEN** o sistema retorna erro 404 "Transação não encontrada"

### Requirement: Consultar status da transação Pix
O sistema DEVE permitir consultar o status atual de uma transação Pix associada a uma comanda.

#### Scenario: Consulta com sucesso
- **WHEN** o garçom consulta o status Pix de uma comanda
- **THEN** o sistema retorna o status da TransacaoPix (AGUARDANDO/CONFIRMADO/EXPIROU)
- **AND** retorna o QR Code Base64 se ainda estiver AGUARDANDO

#### Scenario: Comanda sem transação Pix
- **WHEN** o garçom consulta o status Pix de uma comanda que nunca teve Pix gerado
- **THEN** o sistema retorna erro 404 "Nenhuma transação Pix encontrada para esta comanda"

### Requirement: Fechar comanda com forma de pagamento
O sistema DEVE aceitar a forma de pagamento ao fechar uma comanda manualmente.

#### Scenario: Fechar comanda com MAQUININHA
- **WHEN** o garçom fecha comanda com formaPagamento = MAQUININHA
- **THEN** o sistema altera o status da comanda para PAGA imediatamente

#### Scenario: Fechar comanda com DINHEIRO
- **WHEN** o garçom fecha comanda com formaPagamento = DINHEIRO
- **THEN** o sistema altera o status da comanda para PAGA imediatamente

#### Scenario: Fechar comanda com PIX (via endpoint genérico)
- **WHEN** o garçom tenta fechar comanda com formaPagamento = PIX no endpoint /fechar
- **THEN** o sistema retorna erro 400 "Use o endpoint /pix para gerar QR Code"

#### Scenario: Forma de pagamento inválida
- **WHEN** o garçom envia formaPagamento nulo ou inválido
- **THEN** o sistema retorna erro 422 "Forma de pagamento inválida"

### Requirement: Nova transação se QR Code expirar
O sistema DEVE permitir gerar um novo QR Code se o anterior expirou.

#### Scenario: Regenerar QR Code expirado
- **WHEN** o garçom solicita pagamento Pix para uma comanda AGUARDANDO_PIX
- **AND** a transação anterior está EXPIROU
- **THEN** o sistema cria uma nova transação Pix no Mercado Pago
- **AND** retorna o novo QR Code
