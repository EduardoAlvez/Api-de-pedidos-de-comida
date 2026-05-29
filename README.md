# 🍔 API de Pedidos de Comida

API RESTful completa para sistema de delivery de comida **e comanda digital** (atendimento presencial), desenvolvida com **Spring Boot 3.3.1**.  
O projeto abrange desde cadastro de usuários e restaurantes até criação de pedidos, autenticação via **JWT**, **frete dinâmico por região** e **comanda eletrônica** com rateio de itens compartilhados.

---

## 📌 Sobre o Projeto

API back-end que simula o funcionamento de um aplicativo de delivery **com suporte a atendimento presencial** (comanda digital). Ideal para praças de alimentação (*food courts*) onde cada cliente pode pagar e sair individualmente.

- Usuários podem se cadastrar como **donos de restaurantes**, **clientes** ou **garçons**
- Donos de restaurantes gerenciam estabelecimentos, cardápios e **regiões de entrega**
- Clientes podem fazer pedidos online (**DELIVERY**) mesmo sem login (*guest checkout*)
- Garçons abrem mesas, criam comandas e registram pedidos presenciais (**PRESENCIAL**)
- Itens compartilhados (ex: Coca 2L) são rateados entre as comandas — cada um paga o quanto consumir
- Testes automatizados implementados com auxílio de IA (opencode) para otimizar cobertura e geração de cenários

---

## ⚙️ Funcionalidades

### ✅ Gerenciamento de Usuários
- Cadastro com papéis: **DONO_RESTAURANTE**, **CLIENTE**, **GARCOM**
- CRUD completo de usuários

### ✅ Segurança
- Autenticação **stateless** baseada em **JWT (JSON Web Tokens)** com **auth0/java-jwt 4.4.0**
- Endpoints públicos (`/login`, cadastro) e privados por role
- Criptografia de senhas com **BCrypt**
- Três roles com permissões distintas

### ✅ Gerenciamento de Restaurantes e Produtos
- Donos de restaurantes cadastram e gerenciam seus estabelecimentos
- CRUD completo de produtos (cardápio)

### ✅ Sistema de Pedidos
- Pedidos com **origem**: `DELIVERY` (online) ou `PRESENCIAL` (comanda)
- Criação de pedidos com múltiplos itens
- Suporte para usuários logados e convidados
- Cálculo de subtotal, taxa de entrega e valor total
- Geração de pagamento associado a cada pedido
- Atualização de status do pedido

### ✅ Frete Dinâmico por Região de Entrega
- Restaurantes definem regiões de entrega com **taxa fixa por região**
- CRUD completo de regiões vinculadas ao restaurante
- Cada região possui nome, taxa e raio de abrangência

### ✅ Comanda Digital (Atendimento Presencial)
- **Mesas**: abertura, fechamento e controle de status (`LIVRE`, `OCUPADA`)
- **Comandas individuais**: cada cliente na mesa tem sua própria comanda
- **Rateio compartilhado**: itens divididos (ex: Coca 2L) podem ser rateados — cada cliente paga um valor arbitrário
- **Fechamento individual**: cada cliente paga sua comanda e sai sem precisar dividir igualmente
- Mesa só fica `LIVRE` quando **todas** as comandas estão pagas

### ✅ Tratamento de Exceções
- Exceções personalizadas para erros de negócio (`ValidacaoNegocioException`)
- Handler global com `@ControllerAdvice` para respostas padronizadas

### ✅ Testes Automatizados
- **36 testes** no total (18 unitários + 18 integração)
- Unitários: JUnit 5 + Mockito (camada service)
- Integração: REST Assured + Spring MockMvc (camada controller)
- Relatórios com **Allure** (comportamento + severidade + steps)
- Cobertura com **JaCoCo**
- Branch `QA` dedicada exclusivamente aos testes

### ✅ Documentação
- Documentação interativa com **Swagger/OpenAPI 3** em `/swagger-ui.html`

### ✅ Banco de Dados
- **Spring Profiles** para alternar entre configurações:
  - `h2` — H2 Database (dev, padrão)
  - `mysql` — MySQL (produção)
  - `postgre` — PostgreSQL (produção)

---

## 🔑 Endpoints da API

A API está organizada em torno dos recursos abaixo.  
Todos os endpoints, exceto `/login`, requerem **token JWT** no cabeçalho `Authorization: Bearer <token>`.

### 🔐 Autenticação
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/login` | Autenticação com e-mail e senha → retorna JWT |

### 👤 Usuários
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/usuarios` | Criar usuário (CLIENTE, DONO_RESTAURANTE ou GARCOM) |
| `GET` | `/API/V1/usuarios` | Listar todos os usuários |
| `GET` | `/API/V1/usuarios/{id}` | Buscar usuário por ID |
| `PUT` | `/API/V1/usuarios/{id}` | Atualizar usuário |
| `DELETE` | `/API/V1/usuarios/{id}` | Deletar usuário |

### 🍽️ Restaurantes
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/restaurantes` | Criar restaurante (requer DONO_RESTAURANTE) |
| `GET` | `/API/V1/restaurantes` | Listar restaurantes |
| `GET` | `/API/V1/restaurantes/{id}` | Buscar restaurante por ID |
| `PUT` | `/API/V1/restaurantes/{id}` | Atualizar restaurante |
| `DELETE` | `/API/V1/restaurantes/{id}` | Deletar restaurante |

### 🛒 Produtos (Cardápio)
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/produtos` | Adicionar produto ao cardápio |
| `GET` | `/API/V1/produtos/{id}` | Buscar produto por ID |
| `GET` | `/API/V1/produtos/restaurante/{restauranteId}` | Listar produtos de um restaurante |
| `PUT` | `/API/V1/produtos/{id}` | Atualizar produto |
| `DELETE` | `/API/V1/produtos/{id}` | Deletar produto |

### 📦 Pedidos
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/pedidos` | Criar pedido (DELIVERY ou PRESENCIAL) |
| `GET` | `/API/V1/pedidos/{id}` | Buscar pedido por ID |
| `GET` | `/API/V1/pedidos/usuario/{usuarioId}` | Histórico de pedidos do usuário |
| `PUT` | `/API/V1/pedidos/{id}/status` | Atualizar status do pedido |

### 🚚 Regiões de Entrega (Frete Dinâmico)
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/API/V1/restaurantes/{restauranteId}/regioes` | Listar regiões de entrega |
| `POST` | `/API/V1/restaurantes/{restauranteId}/regioes` | Criar região de entrega |
| `GET` | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Buscar região por ID |
| `PUT` | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Atualizar região |
| `DELETE` | `/API/V1/restaurantes/{restauranteId}/regioes/{id}` | Deletar região |

### 🪑 Mesas
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/mesas` | Abrir mesa |
| `GET` | `/API/V1/mesas` | Listar mesas (filtro: `?restauranteId=`) |
| `GET` | `/API/V1/mesas/{id}` | Buscar mesa por ID |
| `PUT` | `/API/V1/mesas/{id}` | Atualizar mesa |
| `DELETE` | `/API/V1/mesas/{id}` | Fechar/deletar mesa |

### 📋 Comandas
| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/API/V1/mesas/{mesaId}/comandas` | Criar comanda para um cliente na mesa |
| `GET` | `/API/V1/comandas` | Listar comandas (filtro: `?mesaId=`) |
| `GET` | `/API/V1/comandas/{id}` | Buscar comanda por ID |
| `POST` | `/API/V1/comandas/{id}/rateio` | Ratear item compartilhado na comanda |
| `POST` | `/API/V1/comandas/{id}/fechar` | Fechar comanda (pagamento) |

---

## 🛠 Tecnologias Utilizadas

| Categoria | Tecnologia |
|-----------|-----------|
| **Linguagem** | Java 21 |
| **Framework** | Spring Boot 3.3.1 |
| **Persistência** | Spring Data JPA + Hibernate |
| **Web** | Spring Web |
| **Segurança** | Spring Security + Auth0 java-jwt 4.4.0 |
| **Documentação** | SpringDoc OpenAPI 2.3.0 (Swagger) |
| **Banco de Dados** | H2 (dev), MySQL, PostgreSQL |
| **Build** | Maven |
| **Testes Unitários** | JUnit 5 + Mockito |
| **Testes de Integração** | REST Assured + Spring MockMvc |
| **Relatórios** | Allure + JaCoCo |
| **DTO Mapping** | BeanUtils.copyProperties |
| **Lombok** | Redução de boilerplate |

---

## 🏛 Arquitetura e Padrões

- **Arquitetura em Camadas**: Controller → Service → Repository
- **DTO Pattern**: separação entre entidades JPA e objetos de transferência
- **Injeção de Dependências**: com Spring (`@Autowired` / construtor)
- **Tratamento Centralizado de Exceções**: `@ControllerAdvice` com respostas JSON padronizadas
- **Três Roles de Acesso**: `DONO_RESTAURANTE`, `CLIENTE`, `GARCOM`
- **Frete Dinâmico**: cada restaurante define regiões com taxas fixas
- **Comanda Digital**: rateio flexível (valor arbitrário, não divisão igual)
- **Branch Estratégica**: `master` (produção) + `QA` (testes e validação)

---

## 🚀 Como Executar

### Pré-requisitos

- **JDK 21** ou superior
- **Maven 3.8+**
- Cliente de API (Postman, Insomnia, etc.)

### Configuração

```bash
git clone https://github.com/EduardoAlvez/Api-de-pedidos-de-comida.git
```

### Executando (perfil padrão — H2)

```bash
mvn clean install
mvn spring-boot:run
```

A API estará em `http://localhost:8080` e o console H2 em `http://localhost:8080/h2-console`.

### Perfis de Banco de Dados

| Perfil | Banco | Ativação |
|--------|-------|----------|
| `h2` | H2 (dev, padrão) | `mvn spring-boot:run` (default) |
| `mysql` | MySQL | `mvn spring-boot:run -Dspring.profiles.active=mysql` |
| `postgre` | PostgreSQL | `mvn spring-boot:run -Dspring.profiles.active=postgre` |

---

## 🧪 Testes

### Stack

- **Unitários**: JUnit 5 + Mockito (18 testes — services)
- **Integração**: REST Assured + Spring MockMvc (18 testes — controllers)
- **Relatório de comportamento**: Allure (features, severidades, steps)
- **Cobertura**: JaCoCo
- **Implementação assistida**: os testes foram gerados e refinados com auxílio de IA (opencode), acelerando a criação de cenários e garantindo aderência às especificações do projeto

### Executar todos os testes

```bash
mvn clean test
```

### Relatórios

- **Allure**: `target/site/allure-maven-plugin/index.html`
- **JaCoCo**: `target/site/jacoco/index.html`

```bash
# Gerar relatório Allure completo
mvn allure:report
```

---

## 🌿 Estratégia de Branches

- **`master`** — código de produção (funcionalidades estáveis)
- **`QA`** — branch de testes (contém todos os commits de teste + metadados Allure)

As funcionalidades são desenvolvidas em feature branches e mescladas no `master`. Os commits seguem o padrão:

```
TIPO: descrição
```

Onde `TIPO` pode ser: `FEATURE`, `FIX`, `TEST`, `REFACTOR`, `DOCS`, etc.  
Consulte `COMMIT_CONVENTION.md` para detalhes completos.

---

## 🐳 Docker (em desenvolvimento)

### Pré-requisitos

- **Docker** instalado

### Passo 1: Gerar o JAR

```bash
mvn clean package -DskipTests
docker-compose up --build
```

> ⚠️ O suporte a Docker ainda está em fase de ajustes.

---

## 📄 Licença

Projeto acadêmico para Trabalho de Conclusão de Curso (TCC).
