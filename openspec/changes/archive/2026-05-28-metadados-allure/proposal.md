## Why

O relatório Allure atual exibe apenas nomes de testes, status e tempo — uma lista plana sem organização hierárquica. Para o TCC, precisamos de um relatório rico que demonstre domínio da ferramenta: agrupamento por funcionalidade, severity, steps detalhados e attachments.

## What Changes

Adicionar anotações Allure (`@Feature`, `@Story`, `@Severity`, `@Description`, `@Step`, `@Attachment`) em todos os 10 arquivos de teste para enriquecer o relatório.

- **`@Feature`** em cada classe de teste (ex: "Comandas", "Mesas", "Autenticação")
- **`@Story`** para agrupar cenários dentro de uma feature (ex: "Criação", "Rateio", "Fechamento")
- **`@Severity`** em cada método (`BLOCKER`/`CRITICAL`/`NORMAL`/`MINOR`/`TRIVIAL`)
- **`@Step`** nos cenários de múltiplas etapas (ex: criar comanda → rateio → fechar)
- **`@Attachment`** helper no `BaseControllerTest` para capturar payload de request/response
- **`@Description`** nos cenários mais complexos

Nenhuma lógica de teste é alterada — apenas metadados de report.

## Capabilities

### New Capabilities
- `metadados-allure-testes`: Anotações Allure enriquecendo o relatório com behaviors, severity, steps e attachments

### Modified Capabilities

Nenhuma — não há mudança em requisitos de spec existentes.

## Impact

- **10 arquivos** de teste modificados (~60 linhas novas de anotações)
- Nenhuma dependência nova — `allure-junit5` já está no `pom.xml`
- Nenhuma alteração em código de produção
- Relatório Allure passará a exibir behaviors, severity, steps e attachments
