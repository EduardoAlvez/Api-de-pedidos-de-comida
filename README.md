# 🍔 API de Pedidos de Comida

API RESTful completa para um sistema de delivery de comida, desenvolvida com **Spring Boot**.  
O projeto abrange desde o cadastro de usuários e restaurantes até a criação de pedidos e autenticação segura via **JWT**.

---

## 📌 Sobre o Projeto
Este projeto consiste em uma **API back-end** que simula o funcionamento de um aplicativo de delivery.  

- Usuários podem se cadastrar como **donos de restaurantes** ou **clientes**.  
- Donos de restaurantes podem gerenciar seus estabelecimentos e cardápios.  
- Clientes podem visualizar restaurantes, produtos e realizar pedidos, mesmo sem estarem logados (**guest checkout**).

---

## ⚙️ Funcionalidades

### ✅ Gerenciamento de Usuários
- Cadastro de usuários com papéis (**DONO_RESTAURANTE**, **CLIENTE**).  
- CRUD completo de usuários.  

### ✅ Segurança
- Autenticação **stateless** baseada em **JWT (JSON Web Tokens)**.  
- Endpoints públicos (/login, cadastro) e privados.  
- Criptografia de senhas com **BCrypt**.  

### ✅ Gerenciamento de Restaurantes e Produtos
- Donos de restaurantes podem cadastrar e gerenciar seus estabelecimentos.  
- CRUD completo de produtos (cardápio).  

### ✅ Sistema de Pedidos
- Criação de pedidos complexos com múltiplos itens.  
- Suporte para pedidos de usuários logados e convidados.  
- Cálculo de **subtotal, taxa de entrega e valor total**.  
- Geração de um **Pagamento** associado a cada pedido.  
- Atualização de status do pedido.

## 🔑 Principais Endpoints

A API está organizada em torno dos seguintes recursos principais.  
Todos os endpoints, exceto **/login** e o **cadastro de usuários**, requerem um **token JWT** no cabeçalho `Authorization`.

### 🔐 Autenticação
- `POST /login` → Realiza a autenticação de um usuário com e-mail e senha, retornando um token JWT.  

### 👤 Usuários
- `POST /usuarios` → Cria um novo usuário (**CLIENTE** ou **DONO_RESTAURANTE**).  
- `GET /usuarios` → Lista todos os usuários cadastrados.  
- `GET /usuarios/{id}` → Busca um usuário específico pelo ID.  
- `PUT /usuarios/{id}` → Atualiza os dados de um usuário.  
- `DELETE /usuarios/{id}` → Deleta um usuário.  

### 🍽️ Restaurantes
- `POST /restaurantes` → Cria um novo restaurante (**requer DONO_RESTAURANTE autenticado**).  
- `GET /restaurantes` → Lista todos os restaurantes.  
- `GET /restaurantes/{id}` → Busca um restaurante específico.  
- `PUT /restaurantes/{id}` → Atualiza os dados de um restaurante.  
- `DELETE /restaurantes/{id}` → Deleta um restaurante.  

### 🛒 Produtos (Cardápio)
- `POST /produtos` → Adiciona um novo produto ao cardápio de um restaurante.  
- `GET /produtos/{id}` → Busca um produto específico pelo ID.  
- `PUT /produtos/{id}` → Atualiza os dados de um produto.  
- `DELETE /produtos/{id}` → Deleta um produto.  
- `GET /

### 📦 Pedidos
- `POST /pedidos` → Cria um novo pedido (pode ser feito por usuário logado ou convidado).  
- `GET /pedidos/{id}` → Busca um pedido específico pelo seu ID.  
- `GET /pedidos/usuario/{usuarioId}` → Lista o histórico de pedidos de um usuário.  
- `PUT /pedidos/{id}/status` → Atualiza o status de um pedido (ex: CONFIRMADO, CANCELADO, etc.).  



### ✅ Tratamento de Exceções
- Exceções personalizadas para erros de negócio.  
- Handler global com `@ControllerAdvice` para respostas padronizadas.  

### ✅ Documentação
- Documentação interativa com **Swagger/OpenAPI 3**.  TÁ BUGADA!!!

### ✅ Banco de Dados
- Uso de **Spring Profiles** para alternar facilmente entre diferentes configurações de banco de dados.  
- **Perfil de desenvolvimento (dev)** com **H2 Database** em memória para agilidade nos testes e desenvolvimento local (com console acessível em `/h2-console`).  
- **Perfil de produção/docker (postgre)** configurado para **PostgreSQL**, garantindo a persistência dos dados em um ambiente robusto.  

---

## 🛠 Tecnologias Utilizadas
- **Java 21**  
- **Spring Boot 3.2.5**  
- **Spring Data JPA & Hibernate**  
- **Spring Web**  
- **Spring Security**  
- **Lombok**  
- **PostgreSQL** (produção)  
- **H2 Database** (testes)  
- **Maven** (gerenciamento de dependências)  
- **Auth0 Java JWT** (JWT)  
- **SpringDoc OpenAPI** (Swagger)  

---

## 🏛 Arquitetura e Padrões
- **Arquitetura em Camadas**: Controller, Service, Repository.  
- **DTO Pattern**: separação de entidades e objetos de transferência.  
- **Injeção de Dependências (DI)**: com Spring.  
- **Tratamento Centralizado de Exceções** com `@ControllerAdvice`.  
- **Mapeamento de Entidades**: uso de `@JsonManagedReference` e `@JsonBackReference` para evitar recursão infinita.  

---

## 🚀 Como Executar

### Pré-requisitos
- **JDK 21** ou superior  
- **Maven 3.8+**  
- Um cliente de API (**Postman**, **Insomnia**, etc.)  

### Configuração
Clone o repositório:
```bash
git clone https://github.com/EduardoAlvez/Api-de-pedidos-de-comida.git
```
### Executando a Aplicação

Compile o projeto:
```bash
mvn clean install
mvn spring-boot:run
```
Ou rode a classe principal PedidoServiceApplication.java diretamente pela sua IDE.

## 🐳 Executando com Docker [TA BUGADO]

### Pré-requisitos
- **Docker** instalado  

### Passo 1: Gerar o JAR da aplicação
Antes de rodar o container, compile o projeto e gere o **.jar**:
```
mvn clean package -DskipTests

docker-compose up --build
```
## ➡️ A API estará disponível em: 
```
http://localhost:8080
```
