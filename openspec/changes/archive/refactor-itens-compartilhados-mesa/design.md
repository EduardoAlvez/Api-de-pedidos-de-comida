## Context

Atualmente a mesa (`Mesa`) possui apenas `nomeCliente`, `status`, `dataAbertura` e `restaurante`. Itens compartilhados são representados pelo flag `compartilhado` em `ComandaItem`, o que os vincula a uma comanda individual em vez de à mesa como um todo. O rateio (`ComandaService.rateio`) percorre todas as comandas da mesa filtrando `compartilhado=true` para calcular o saldo.

A mudança extrai os itens compartilhados para uma entidade própria ligada à `Mesa`, tornando o modelo mais semântico e flexível.

## Goals / Non-Goals

**Goals:**
- Criar entidade `ItemCompartilhado` ligada à `Mesa`
- Remover o flag `compartilhado` de `ComandaItem`
- Adicionar endpoints para CRUD de itens compartilhados na mesa
- Adaptar o rateio para consultar o pool de itens compartilhados da mesa
- Adicionar `nomeCliente` (responsável) como campo obrigatório na criação da mesa (já existe, mas garantir que é preenchido)
- Inicializar `itensCompartilhados` como lista vazia ao criar mesa

**Non-Goals:**
- Não alterar o fluxo de pagamento PIX
- Não alterar o fluxo de pedidos delivery
- Não alterar autenticação/autorização
- Não adicionar rateio automático (permanece manual)

## Decisions

1. **Nova entidade separada vs. tabela de relacionamento muitos-para-muitos**
   → Optou-se por entidade própria `ItemCompartilhado` com `id`, `produto`, `quantidade`, `precoUnitario`, `mesa`. É mais simples que uma relação M:N e permite adicionar metadados futuros (ex: quem adicionou, observação).

2. **Endpoints em `MesaController` vs. `ItemCompartilhadoController` separado**
   → Os endpoints serão adicionados no `MesaController` seguindo o padrão `/API/V1/mesas/{mesaId}/compartilhados`. Evita criar um controller novo e mantém consistência com `/API/V1/mesas/{mesaId}/comandas`.

3. **Rateio permanece validando contra a mesa**
   → O método `rateio` em `ComandaService` passa a consultar `mesa.itensCompartilhados` em vez de percorrer comandas filtrando `compartilhado=true`. O resto da lógica (validar saldo, registrar `ComandaRateio`) permanece idêntico.

4. **DTOs de response incluem itens compartilhados**
   → `MesaResponseDTO` ganha um campo `List<ItemCompartilhadoResponseDTO> itensCompartilhados` para que o frontend exiba o pool da mesa.

5. **Limpeza automática ao fechar última comanda**
   → Quando a última comanda da mesa tem seu status alterado para `PAGA` (via `/fechar`), todos os `ItemCompartilhado` da mesa são deletados. Isso evita "sujeira" se o garçom reutilizar a mesma mesa para um novo grupo. As comandas e rateios individuais permanecem inalterados (histórico preservado).

## Risks / Trade-offs

- **Mudança BREAKING no `ComandaItem`**: Testes e código existentes que usam `isCompartilhado()` quebrarão. → Correção direta: remover o campo e ajustar referências.
- **Dados existentes em banco**: Se houver comandas com `compartilhado=true` em produção, é necessário migrar esses itens para a nova tabela. → Para ambiente dev (H2 em memória) não há impacto.
- **Rateio existente**: ComandaRateio registra pagamentos por produto — não há perda de dados, pois a validação de saldo muda de fonte (comanda → mesa) mas o registro do rateio permanece na comanda.
