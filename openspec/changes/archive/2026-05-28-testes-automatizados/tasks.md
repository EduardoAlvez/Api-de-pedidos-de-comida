## 1. Configuração Inicial

- [x] 1.1 Adicionar dependências REST Assured no `pom.xml`: `io.rest-assured:rest-assured:5.4.0` e `io.rest-assured:spring-mock-mvc:5.4.0`
- [x] 1.2 Adicionar dependência Allure no `pom.xml`: `io.qameta.allure:allure-junit5:2.27.0`
- [x] 1.3 Adicionar plugin JaCoCo no `pom.xml`: `org.jacoco:jacoco-maven-plugin:0.8.12` configurado no `prepare-agent` e `report` goals
- [x] 1.4 Criar `src/test/resources/application-test.properties` com configuração H2 e JPA create-drop
- [x] 1.5 Criar classes base para testes: `BaseServiceTest` (config Mockito) e `BaseControllerTest` (config REST Assured + MockMvc)
- [ ] 1.6 Verificar compilação e testes com `mvnw test`

## 2. Testes Unitários - ComandaService

- [x] 2.1 Criar `ComandaServiceTest.java`: teste de criar comanda em mesa LIVRE (→ OCUPADA)
- [x] 2.2 Criar teste: criar comanda em mesa OCUPADA (permanece OCUPADA)
- [x] 2.3 Criar teste: rateio com valor que não excede saldo (aceito)
- [x] 2.4 Criar teste: rateio com valor que excede saldo (rejeitado com erro)
- [x] 2.5 Criar teste: fechar comanda (status → PAGA, dataFechamento preenchida)
- [x] 2.6 Criar teste: fechar comanda já PAGA (exceção)
- [x] 2.7 Criar teste: fechar última comanda ABERTA (mesa → LIVRE)
- [x] 2.8 Criar teste: fechar comanda com outras ABERTAS (mesa permanece OCUPADA)

## 3. Testes Unitários - MesaService

- [x] 3.1 Criar `MesaServiceTest.java`: teste de criar mesa com dados válidos
- [x] 3.2 Criar teste: buscar mesa por ID existente
- [x] 3.3 Criar teste: buscar mesa por ID inexistente (exceção)

## 4. Testes Unitários - RegiaoEntregaService

- [x] 4.1 Criar `RegiaoEntregaServiceTest.java`: teste de listar/buscar/criar regiões
- [x] 4.2 Criar teste: buscar região inexistente (exceção)

## 5. Testes Unitários - PedidoService

- [x] 5.1 Criar `PedidoServiceTest.java`: teste de criar pedido delivery com dados válidos
- [x] 5.2 Criar teste: criar pedido com região inválida (exceção)

## 6. Testes de Integração - ComandaController

- [x] 6.1 Criar `ComandaControllerTest.java`: teste POST /API/V1/mesas/{id}/comandas → 201
- [x] 6.2 Criar teste: POST com produto inexistente → 404
- [x] 6.3 Criar teste: GET /API/V1/comandas?mesaId={id} → 200 (lista)
- [x] 6.4 Criar teste: fluxo completo (criar comanda → rateio → fechar)
- [x] 6.5 Criar teste: rateio com valor excessivo → 422

## 7. Testes de Integração - MesaController

- [x] 7.1 Criar `MesaControllerTest.java`: teste POST /API/V1/mesas → 201
- [x] 7.2 Criar teste: GET /API/V1/mesas?restauranteId={id} → 200

## 8. Testes de Integração - PedidoController (Delivery)

- [x] 8.1 Criar `PedidoControllerTest.java`: teste POST /API/V1/pedidos → 201
- [x] 8.2 Criar teste: POST /API/V1/pedidos sem endereço → 422

## 9. Testes de Integração - AutenticacaoController

- [x] 9.1 Criar `AutenticacaoControllerTest.java`: teste login com credenciais válidas → 200 + token
- [x] 9.2 Criar teste: login com senha inválida → 403

## 10. Testes de Segurança

- [x] 10.1 Criar `SecurityTest.java`: testes com roles GARCOM/CLIENTE/sem token
- [x] 10.2 Criar teste: criar mesa como GARCOM → 201, como CLIENTE → 403
- [x] 10.3 Criar teste: criar pedido como CLIENTE → 201
- [x] 10.4 Criar teste: token inválido → 401

## 11. Relatórios

- [x] 11.1 Executar `mvnw test` e verificar que todos os testes passam
- [x] 11.2 Gerar relatório JaCoCo: `mvnw jacoco:report` e verificar HTML em `target/site/jacoco/`
- [x] 11.3 Gerar relatório Allure: `mvnw allure:report` e verificar HTML em `target/site/allure-maven-plugin/`
- [x] 11.4 Verificar que testes não alteram código de produção
