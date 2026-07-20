## ADDED Requirements

### Requirement: Anotações @Feature em classes de teste

Cada classe de teste DEVE ter uma anotação `@Feature` com o nome da funcionalidade que está sendo testada.

#### Scenario: Feature definida na classe
- **WHEN** a classe de teste existe
- **THEN** ela DEVE estar anotada com `@Feature("nome-da-funcionalidade")`

### Requirement: Anotações @Severity em métodos de teste

Cada método de teste DEVE ter uma anotação `@Severity` indicando a criticidade do cenário.

#### Scenario: Severity BLOCKER
- **WHEN** o teste cobre criação de recurso principal, autenticação ou segurança
- **THEN** DEVE usar `@Severity(SeverityLevel.BLOCKER)`

#### Scenario: Severity CRITICAL
- **WHEN** o teste cobre operações de negócio (rateio, fechamento, fluxo completo)
- **THEN** DEVE usar `@Severity(SeverityLevel.CRITICAL)`

#### Scenario: Severity NORMAL
- **WHEN** o teste cobre listagens, consultas ou validações de dados
- **THEN** DEVE usar `@Severity(SeverityLevel.NORMAL)`

#### Scenario: Severity MINOR
- **WHEN** o teste cobre edge cases ou cenários de erro
- **THEN** DEVE usar `@Severity(SeverityLevel.MINOR)`

### Requirement: Anotações @Step em cenários multi-etapa

Testes que executam múltiplas operações sequenciais DEVEM usar `@Step` para cada etapa.

#### Scenario: Step em fluxo completo
- **WHEN** um teste executa 3+ operações (criar, depois rateio, depois fechar)
- **THEN** cada operação DEVE estar em um método auxiliar anotado com `@Step("descrição")`

### Requirement: Helper @Attachment no BaseControllerTest

O `BaseControllerTest` DEVE ter um método helper anotado com `@Attachment` para capturar payloads.

#### Scenario: Attachment disponível
- **WHEN** um teste de controller precisa capturar request/response
- **THEN** DEVE existir um método `attachmentPayload(String name, String content)` anotado com `@Attachment(value = "{name}", type = "application/json")`
