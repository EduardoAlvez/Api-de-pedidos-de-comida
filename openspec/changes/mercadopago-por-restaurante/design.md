## Context

Atualmente o token do Mercado Pago é único para toda a aplicação, definido via env var `MP_ACCESS_TOKEN`. Isso funciona para um único restaurante, mas não escala para múltiplos restaurantes, cada um com sua própria conta do Mercado Pago.

## Goals / Non-Goals

**Goals:**
- Cada restaurante pode ter seu próprio Access Token e Webhook Secret
- DONO pode configurar/remover credenciais via API
- PIX só fica disponível se o restaurante tiver token configurado
- Webhook identifica o restaurante pelo path
- Nada muda para restaurantes que não configuram PIX

**Non-Goals:**
- Não alterar o fluxo de pagamento presencial (dinheiro/cartão/maquininha)
- Não adicionar suporte a múltiplos webhooks por restaurante
- Não criar interface web (apenas API)

## Decisions

1. **Token na entidade Restaurante vs. tabela separada**
   → Optou-se por campos nullable diretamente em `Restaurante`. São dados simples (2 strings), não justificam tabela separada. O acesso é sempre pelo restaurante logado, então a query é direta.

2. **Webhook com restauranteId no path vs. query parameter**
   → Path (`POST /pix/webhook/{restauranteId}`). Mais RESTful, e evita problemas com Mercado Pago rewritando query params.

3. **PIXClient recebe token como parâmetro vs. injeção de dependência**
   → Parâmetro no método `criarCobranca(valor, descricao, accessToken)`. Mais simples que criar um provider/factory. O token é uma string, não um bean.

4. **Remover MercadoPagoConfigSetup vs. deixar como fallback**
   → Remover. Se não há mais token global, a classe não faz sentido. Cada chamada ao PIX leva seu próprio token.

5. **Proteção na ausência de token**
   → `ComandaService.fechar()` (quando `formaPagamento == PIX`) verifica se `restaurante.mpAccessToken != null`. Se null, retorna 400 "PIX não configurado para este restaurante". O frontend deve consultar isso antes de exibir a opção PIX.

## Risks / Trade-offs

- **Mudança BREAKING na rota do webhook**: URLs existentes no Mercado Pago precisam ser atualizadas de `/pix/webhook` para `/pix/webhook/{id}`.
- **MercadoPagoPixClient**: Método `criarCobranca` muda de assinatura — todos os callers precisam ser atualizados.
- **Webhook sem restauranteId**: Se o Mercado Pago chamar a URL antiga (sem o id), o backend deve rejeitar com 400. Não há fallback seguro.
