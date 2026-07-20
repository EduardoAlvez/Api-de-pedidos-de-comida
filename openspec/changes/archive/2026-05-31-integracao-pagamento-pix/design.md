## Context

A API atualmente permite fechar comandas manualmente via `POST /comandas/{id}/fechar`, que muda o status para `PAGA` imediatamente. Não há integração com meios de pagamento digitais. A `FormaPagamento` existe no enum mas não é usada no fluxo de comandas (apenas em `Pagamento`, que é vinculado a `Pedido` de delivery).

O Mercado Pago foi escolhido como PSP por ter SDK Java oficial, taxa 0% no QR Code presencial, e webhook robusto.

## Goals / Non-Goals

**Goals:**
- Gerar QR Code Pix dinâmico via API do Mercado Pago ao fechar comanda
- Receber webhook do Mercado Pago e atualizar comanda para PAGA automaticamente
- Permitir consulta de status da transação Pix
- Manter compatibilidade com fluxos existentes (maquininha, dinheiro)
- Adicionar `AGUARDANDO_PIX` como novo status intermediário

**Non-Goals:**
- Não inclui integração com cartão de crédito/débito online (só maquininha física)
- Não inclui split de pagamento
- Não inclui estorno/reembolso
- Não inclui geração de QR Code estático (só dinâmico por transação)

## Decisions

### 1. SDK Mercado Pago via REST client próprio vs SDK oficial
- **Decisão:** Usar SDK oficial `com.mercadopago:sdk`
- **Motivo:** SDK oficial abstrai autenticação, serialização, e tratamentos de erro. Menos código para manter.
- **Alternativa:** Chamadas REST diretas com RestTemplate/WebClient — mais flexível, mas mais código.

### 2. Nova entidade `TransacaoPix` vs reutilizar `Pagamento`
- **Decisão:** Criar `TransacaoPix` separada
- **Motivo:** `Pagamento` está vinculado a `Pedido` (delivery) via `@OneToOne`. Comanda tem fluxo diferente (rateio, compartilhados). Misturar os dois aumentaria complexidade.
- **Alternativa:** Adicionar campo `transacaoPixId` em `Pagamento` — mas quebraria o modelo existente.

### 3. `AGUARDANDO_PIX` como status de Comanda vs campo separado
- **Decisão:** Novo status `AGUARDANDO_PIX` em `StatusComanda`
- **Motivo:** O fluxo inteiro depende do status — app precisa saber se a comanda está aguardando pagamento para exibir QR Code, permitir cancelamento, etc.
- **Alternativa:** Campo `boolean pixAguardando` — menos semântico, mais propenso a estados inconsistentes.

### 4. Webhook com verificação de assinatura
- **Decisão:** Validar assinatura HMAC-SHA256 enviada no header `X-Signature` do Mercado Pago
- **Motivo:** Segurança — garante que a notificação veio do Mercado Pago e não foi adulterada.

### 5. Endpoints

| Endpoint | Método | Descrição |
|---|---|---|
| `/API/V1/comandas/{id}/pix` | POST | Gera QR Code Pix e muda status para AGUARDANDO_PIX |
| `/API/V1/comandas/{id}/pix` | GET | Retorna status atual da transação Pix |
| `/API/V1/pix/webhook` | POST | Recebe notificação do Mercado Pago |
| `/API/V1/comandas/{id}/fechar` | PUT (modificado) | Agora aceita `formaPagamento` no body |

### 6. Fluxo de fechamento atualizado

**Maquininha / Dinheiro:**
`PUT /comandas/{id}/fechar` com `{ "formaPagamento": "MAQUININHA" }` → status vai direto para PAGA (comportamento atual)

**Pix:**
`POST /comandas/{id}/pix` → gera QR Code → status vira AGUARDANDO_PIX → webhook confirma → status vira PAGA

### 7. Modelo `TransacaoPix`

```
TransacaoPix
├── id: Long (PK)
├── comandaId: Long (FK → Comanda)
├── valor: BigDecimal (valor cobrado via Pix — pode ser parcial)
├── qrCodeBase64: TEXT (imagem do QR Code)
├── payloadCopiaCola: TEXT (código Pix copia e cola)
├── txId: String (ID da transação no Mercado Pago)
├── status: StatusTransacaoPix (AGUARDANDO, CONFIRMADO, EXPIROU)
├── dataCriacao: LocalDateTime
├── dataConfirmacao: LocalDateTime (nullable)
```

`StatusTransacaoPix`: `AGUARDANDO`, `CONFIRMADO`, `EXPIROU`

### 8. Cálculo do valor do Pix

O valor do QR Code Pix NÃO é necessariamente o `valorTotal` da comanda. O sistema calcula o **saldo restante**:

```
saldoRestante = comanda.valorTotal - totalRateiosJaPagos
```

Onde `totalRateiosJaPagos` é a soma de todos os `ComandaRateio.valorPago` vinculados à comanda.

**Exemplo:**
- Comanda: R$ 60,00
- Cliente já pagou R$ 20,00 via rateio (maquininha)
- Pix gera QR Code de R$ 40,00

Isso permite que o cliente pague parte com maquininha/dinheiro e o restante com Pix.

### 9. Atualização no `fechar` da Comanda

O método `fechar` no `ComandaService` passará a aceitar um parâmetro `FormaPagamento`:
- Se `PIX`: lança erro — Pix deve usar o endpoint específico
- Se `MAQUININHA` ou `DINHEIRO`: fluxo atual (status → PAGA direto)

O controller `ComandaController` será atualizado para aceitar um DTO com `formaPagamento` opcional (default `MAQUININHA` para não quebrar clientes existentes).

## Risks / Trade-offs

- **[Webhook pode falhar]** → Mitigação: endpoint `GET /comandas/{id}/pix` permite consulta manual. Garçom pode remarcar como pago manualmente se necessário.
- **[QR Code expira]** → O Mercado Pago define tempo de expiração. Se expirar, garçom gera novo QR Code.
- **[SDK Mercado Pago desatualizado]** → SDK Java é mantido pelo MP, mas mudanças na API podem exigir atualizações. Mitigação: depender de interfaces próprias (PixClient) com implementação injetada, facilitando troca de PSP.
- **[Novo status AGUARDANDO_PIX não migrado]** → Bancos H2 recriam do zero (create-drop). Para MySQL/PostgreSQL, ddl-auto=update adiciona o novo valor automaticamente no enum.
