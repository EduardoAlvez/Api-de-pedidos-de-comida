## 1. Helper @Attachment no BaseControllerTest

- [x] 1.1 Adicionar método `attachmentPayload()` com `@Attachment` em `BaseControllerTest.java`

## 2. Controller Tests — ComandaControllerTest

- [x] 2.1 Adicionar imports Allure (`@Feature`, `@Story`, `@Severity`, `@Step`, `@Description`)
- [x] 2.2 Anotar classe com `@Feature("Comandas")`
- [x] 2.3 Anotar métodos de teste com `@Severity` e `@Story` adequados
- [x] 2.4 Adicionar `@Step` no fluxo completo (criar → rateio → fechar)
- [x] 2.5 Adicionar `@Description` no cenário de fluxo completo

## 3. Controller Tests — MesaController, PedidoController, AutenticacaoController

- [x] 3.1 `MesaControllerTest.java`: `@Feature("Mesas")`, `@Severity` em cada método
- [x] 3.2 `PedidoControllerTest.java`: `@Feature("Pedidos Delivery")`, `@Severity` em cada método
- [x] 3.3 `AutenticacaoControllerTest.java`: `@Feature("Autenticação")`, `@Severity` em cada método

## 4. Security Test

- [x] 4.1 `SecurityTest.java`: `@Feature("Segurança")`, `@Severity(BLOCKER)` nos cenários principais

## 5. Service Tests — ComandaServiceTest

- [x] 5.1 Adicionar imports Allure
- [x] 5.2 Anotar classe com `@Feature("Comandas")`
- [x] 5.3 Anotar cada método com `@Severity` e `@Story` adequados
- [x] 5.4 Adicionar `@Step` nos cenários de múltiplas operações

## 6. Service Tests — MesaService, PedidoService, RegiaoEntregaService

- [x] 6.1 `MesaServiceTest.java`: `@Feature("Mesas")`, `@Severity`
- [x] 6.2 `PedidoServiceTest.java`: `@Feature("Pedidos")`, `@Severity`, `@Step`
- [x] 6.3 `RegiaoEntregaServiceTest.java`: `@Feature("Regiões de Entrega")`, `@Severity`

## 7. Sanity Test

- [x] 7.1 `PedidoServiceApplicationTests.java`: `@Feature("Aplicação")`, `@Severity(TRIVIAL)`

## 8. Verificação Final

- [x] 8.1 Executar `mvnw test` e confirmar 0 falhas (36/36)
- [x] 8.2 Executar `mvnw allure:report` e confirmar metadados nos resultados JSON
