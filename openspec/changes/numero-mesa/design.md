## Context

Atualmente `Mesa` não tem número. O front-end inventa um identificador visual que vira `nomeCliente`. Quando o garçom quer se referir à "mesa 5", ele precisa olhar o `id` ou o `nomeCliente`, causando confusão.

## Goals / Non-Goals

**Goals:**
- Adicionar campo `numero` em Mesa (opcional, nullable)
- Validar unicidade por restaurante
- Expor no response da API
- Aceitar no request (opcional — se não enviado, fica null)

**Non-Goals:**
- Não alterar a lógica de criação de comandas/PIX
- Não alterar autenticação/autorização
- Não renumerar mesas existentes

## Decisions

1. **Campo nullable vs. obrigatório**
   → Nullable. Mesas existentes não precisam ser migradas. O front-end pode ou não enviar o número.

2. **Unicidade por restaurante vs. global**
   → Por restaurante. Cada restaurante tem sua própria numeração (ex: mesa 1, 2, 3).

3. **Validação no service vs. unique constraint no banco**
   → Ambos. Unique constraint `(restaurante_id, numero)` no banco + validação no service para mensagem de erro amigável.

4. **Renumeração automática vs. manual**
   → Manual. O garçom digita o número que quiser. Sem auto-incremento.

## Risks

- Se dois garçons abrirem mesa com o mesmo número ao mesmo tempo, a validação no service pode falhar (race condition). A unique constraint no banco cobre esse caso com erro 400.
- Mesas sem número (null) continuam funcionando normalmente.
