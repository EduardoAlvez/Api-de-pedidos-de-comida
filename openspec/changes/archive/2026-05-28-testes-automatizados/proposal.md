## Why

O projeto atualmente possui apenas um teste automatizado (`contextLoads`). As funcionalidades de delivery e comanda digital foram implementadas e validadas manualmente, mas sem cobertura automatizada. Conforme a aplicação cresce, testes manuais se tornam insustentáveis e o risco de regressão aumenta. Precisamos de uma suíte de testes automatizados (unitários, integração e segurança) para garantir que o sistema continue funcionando conforme novas mudanças forem introduzidas.

## What Changes

- Adicionar dependência do **REST Assured** para testes de integração de controllers
- Criar testes unitários (JUnit + Mockito) para todas as services: `ComandaService`, `MesaService`, `RegiaoEntregaService`, `PedidoService`
- Criar testes de integração (REST Assured) para todos os controllers: `ComandaController`, `MesaController`, `PedidoController`, `RegiaoEntregaController`, `AutenticacaoController`
- Criar testes de segurança para verificar restrição de acesso por role (GARCOM, CLIENTE, DONO_RESTAURANTE)
- Remover dependência total de validação manual para mudanças futuras

## Capabilities

### New Capabilities
- `testes-unitarios`: Testes unitários da camada de serviço com JUnit 5 e Mockito, cobrindo lógica de negócio, validações e regras de rateio/fechamento
- `testes-integracao`: Testes de integração dos endpoints REST com REST Assured e banco H2, validando contratos, status codes e estrutura das respostas
- `testes-seguranca`: Testes de segurança para verificar que cada endpoint respeita as roles corretas (GARCOM, CLIENTE, DONO_RESTAURANTE) e que tokens inválidos/expirados são rejeitados

### Modified Capabilities
Nenhuma — especificações existentes não são alteradas, apenas testadas.

## Impact

- **Dependências**: Adicionar `io.rest-assured:rest-assured`, `io.rest-assured:spring-mock-mvc`, `io.qameta.allure:allure-junit5` e `org.jacoco:jacoco-maven-plugin` ao `pom.xml`
- **Relatórios**: Gerar relatório HTML do Allure com steps e timings dos testes, e relatório JaCoCo com percentual de cobertura por pacote
- **Código existente**: Nenhuma alteração no código de produção. Apenas criação de novos arquivos de teste.
- **Cobertura esperada**: ~80% das services, ~90% dos controllers (fluxos principais + borda)
- **Tempo de build**: Aumento de ~2-3 minutos no `mvnw test`
