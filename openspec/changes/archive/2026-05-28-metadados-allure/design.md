## Context

Atualmente os 36 testes do projeto rodam com JUnit 5 + Mockito (service layer) e REST Assured + MockMvc (controller layer). O `allure-junit5` (2.27.0) já está no `pom.xml` e as execuções geram `target/allure-results/`. Porém nenhum teste possui anotações Allure, resultando em um relatório plano sem organização hierárquica.

## Goals / Non-Goals

**Goals:**
- Adicionar `@Feature`, `@Story`, `@Severity`, `@Description`, `@Step`, `@Attachment` nos 10 arquivos de teste
- Gerar relatório Allure com Behaviors tab organizado por Feature → Story, severity badges, steps expansíveis e attachments
- Nenhuma mudança em lógica de teste ou código de produção

**Non-Goals:**
- Não alterar `pom.xml` (dependências já existem)
- Não criar novos testes — apenas metadados
- Não modificar `testes-automatizados` (já arquivado)

## Decisions

1. **`@Feature` por classe** — Cada classe de teste recebe uma feature única (ex: `@Feature("Comandas")`). Isso agrupa todos os métodos da classe sob o mesmo nó no Behaviors tab.
2. **`@Story` por grupo lógico** — Classes com múltiplos cenários distintos (ex: `ComandaControllerTest` com criação, rateio, fechamento) recebem `@Story` para sub-agrupamento.
3. **`@Severity` por método** — Distribuição: `BLOCKER` para criação/fluxo principal, `CRITICAL` para operações de negócio, `NORMAL` para listagens, `MINOR` para edge cases, `TRIVIAL` para `contextLoads`.
4. **`@Step` apenas em cenários multi-etapa** — `ComandaControllerTest.fluxoCompleto` e métodos de service test. Testes de controller simples (1 requisição) não precisam de step.
5. **`@Attachment` via helper** — Um método `attachmentPayload(String name, String content)` no `BaseControllerTest` anotado com `@Attachment` para capturar JSON de request/response quando necessário.
6. **`@Description` nos cenários principais** — Apenas nos testes mais representativos (10% dos casos) para não poluir.

## Risks / Trade-offs

- **Risco:** Anotações `@Step` quebram se renomearmos métodos. → Mitigação: usar strings literais, não referências.
- **Risco:** Relatório Allure 2.27.0 pode ter incompatibilidade com versões futuras. → Mitigação: versão fixa no `pom.xml`.
- **Trade-off:** `@Step` adiciona boilerplate. → Compensado pelo ganho de visibilidade no relatório do TCC.
