## Context

O projeto atualmente possui 32 tasks de funcionalidade implementadas (delivery + comanda digital), mas apenas 1 teste automatizado. As validações foram feitas manualmente via curl. Precisamos estabelecer uma base sólida de testes automatizados antes que novas funcionalidades sejam adicionadas.

Tecnologias disponíveis atualmente:
- `spring-boot-starter-test` (JUnit 5, Mockito, MockMvc, AssertJ)
- H2 em memória para testes de integração

## Goals / Non-Goals

**Goals:**
- Cobertura de ~80% das services (ComandaService, MesaService, RegiaoEntregaService, PedidoService)
- Cobertura de ~90% dos controllers (fluxos principais + erros + borda)
- Testes de segurança para verificar restrição de acesso por role
- Adicionar REST Assured como dependência de teste para facilitar testes de controller

**Non-Goals:**
- Não alterar código de produção
- Não adicionar cobertura para entidades simples (ex: UsuarioService não será testado agora)
- Não configurar CI/CD neste momento
- Não atingir 100% de cobertura

## Decisions

### 1. REST Assured + Spring Mock MVC (em vez de MockMvc puro ou testes HTTP reais)

**Decisão:** Usar REST Assured com `spring-mock-mvc` (não sobe Tomcat real, usa o dispatcher do Spring).

**Racional:**
- MockMvc puro tem sintaxe verbosa e menos expressiva para validar JSON responses
- REST Assured tem sintaxe `given/when/then` mais limpa e legível
- `spring-mock-mvc` evita a complexidade de subir servidor HTTP real
- Mesma abordagem usada em projetos Spring Boot reais

### 2. Perfil de teste separado (application-test.properties)

**Decisão:** Criar `src/test/resources/application-test.properties` com H2 em modo `mem` e JPA `create-drop`.

**Racional:**
- Isola configuração de teste da de desenvolvimento
- Garante que testes não poluem bancos de desenvolvimento
- JPA cria e derruba schema automaticamente

### 3. Organização de classes de teste por camada

**Decisão:**
```
src/test/java/com/ecommerce/pedido/
├── services/
│   ├── ComandaServiceTest.java
│   ├── MesaServiceTest.java
│   ├── RegiaoEntregaServiceTest.java
│   └── PedidoServiceTest.java
├── controllers/
│   ├── ComandaControllerTest.java
│   ├── MesaControllerTest.java
│   ├── PedidoControllerTest.java
│   ├── RegiaoEntregaControllerTest.java
│   └── AutenticacaoControllerTest.java
└── security/
    └── SecurityTest.java
```

### 4. Estrutura dos testes unitários (Service)

- Usar `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` + `@Mock`
- Testar cenários de sucesso, validação e erro
- Nomear métodos em PT-BR: `deveCriarComandaComSucesso`, `deveLancarExcecaoQuandoProdutoNaoExiste`

### 5. Estrutura dos testes de integração (Controller)

- Usar `@SpringBootTest(webEnvironment = WebEnvironment.MOCK)` + `@AutoConfigureMockMvc`
- REST Assured configurado com `MockMvcRequestSpecification`
- Carregar dados de teste via SQL ou diretamente nos testes
- Cada cenário: Dado/Quando/Então nos comentários

### 6. Allure Framework para relatório de execução

**Decisão:** Adicionar `allure-junit5` e gerar relatório com `mvnw allure:report`.

**Racional:**
- Allure produz relatório HTML interativo com steps, timings, categorias e histórico
- Integração nativa com JUnit 5 via anotação `@AllureId`, `@Step`, `@Feature`
- Ideal para o TCC — visual profissional que impressiona banca
- Comandos:
  ```bash
  mvnw test                    # Executa testes + gera resultados Allure em target/allure-results/
  mvnw allure:report           # Gera HTML em target/site/allure-report/
  mvnw allure:serve            # Sobe servidor local para visualizar
  ```

### 7. JaCoCo para relatório de cobertura

**Decisão:** Adicionar `jacoco-maven-plugin` e gerar relatório com `mvnw jacoco:report`.

**Racional:**
- JaCoCo é o padrão da indústria para cobertura de código em Java
- Gera relatório HTML colorido mostrando linhas cobertas/não cobertas por pacote
- Perfeito para medir e apresentar cobertura no TCC
- Comandos:
  ```bash
  mvnw test                    # Executa testes (JaCoCo coleta dados automaticamente)
  mvnw jacoco:report           # Gera HTML em target/site/jacoco/
  ```

### 8. Estrutura dos testes de segurança

- Usar `@WithMockUser` ou criar tokens JWT programaticamente
- Para cada endpoint da comanda: testar com role GARCOM (200), CLIENTE (403), DONO_RESTAURANTE (403)
- Para endpoints de delivery: testar com CLIENTE (200), GARCOM (403)
- Testar token inválido → 401, token ausente → 401

## Risks / Trade-offs

- **Risco:** REST Assured com spring-mock-mvc pode ter diferenças sutis de comportamento vs HTTP real
  - **Mitigação:** Para testes críticos de contrato, podemos complementar com testes manuais periódicos
- **Risco:** Testes de integração lentos
  - **Mitigação:** Usar `@SpringBootTest` apenas para controllers; services usam `@ExtendWith(MockitoExtension.class)` que é muito rápido
- **Risco:** Falso positivo em testes de segurança (teste passa mas endpoint está inseguro)
  - **Mitigação:** Testar explicitamente 401, 403 e 200 para cada combinação de role
