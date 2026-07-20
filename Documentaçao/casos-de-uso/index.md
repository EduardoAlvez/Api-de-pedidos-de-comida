# Casos de Uso — Comanda Digital

> **Escopo:** Backend presencial (mesas, comandas, garçons, pagamentos).
> **Fontes:** Requisitos funcionais RF001–RF050 + specs OpenSpec.
> **Diagrama UML:** `diagrama-casos-de-uso.puml`

---

## Atores

| Ator | Descrição |
|---|---|
| **CLIENTE** | Usuário que se cadastra e faz pedidos delivery |
| **GARCOM** | Funcionário que atende mesas, cria comandas e processa pagamentos |
| **DONO_RESTAURANTE** | Gestor do restaurante; gerencia garçons, mesas e encerra contas |
| **Mercado Pago** | Sistema externo de pagamento (webhook de confirmação PIX) |
| **Visitante** | Usuário não autenticado (cadastro e login) |

---

## Casos de Uso

### CU-01: Cadastrar Usuário (CLIENTE)

| Campo | Valor |
|---|---|
| **ID** | CU-01 |
| **Atores** | Visitante |
| **Pré-condições** | Usuário não possui conta |
| **Pós-condições** | Usuário criado com role CLIENTE |
| **RFs** | RF046 |
| **Spec** | spec/usuarios |

**Fluxo Principal:**
1. Visitante envia POST `/API/V1/usuarios` com nome, email, telefone e senha.
2. Sistema valida campos obrigatórios.
3. Sistema verifica que o email não está duplicado.
4. Sistema hashea a senha com BCrypt.
5. Sistema cria o usuário com `tipo = CLIENTE` (ignora campo `tipo` do payload).
6. Sistema retorna 201 Created com id, nome, email, telefone e tipo.

**Fluxos Alternativos:**
- **3a.** Email já cadastrado → 409 Conflict "Este e-mail já está cadastrado."
- **2a.** Campos obrigatórios faltando ou inválidos → 400 Bad Request com mensagens de validação.

---

### CU-02: Login

| Campo | Valor |
|---|---|
| **ID** | CU-02 |
| **Atores** | CLIENTE, GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Usuário possui conta |
| **Pós-condições** | Token JWT válido por 2 horas |
| **RFs** | RF039, RF042 |
| **Spec** | spec/usuarios, spec/autenticacao-garcom |

**Fluxo Principal:**
1. Usuário envia POST `/login` com email e senha.
2. Sistema valida credenciais.
3. Sistema gera token JWT com subject=email, issuer="API Pedidos E-commerce", expiração 2h.
4. Sistema retorna 200 OK com `{ "token": "...", "type": "Bearer" }`.

**Fluxos Alternativos:**
- **2a.** Credenciais inválidas → 401 Unauthorized.

---

### CU-03: Criar Garçom

| Campo | Valor |
|---|---|
| **ID** | CU-03 |
| **Atores** | DONO_RESTAURANTE |
| **Pré-condições** | Dono autenticado; restaurante existe |
| **Pós-condições** | Garçom criado e vinculado ao restaurante |
| **RFs** | RF045 |
| **Spec** | spec/garcons |

**Fluxo Principal:**
1. Dono envia POST `/API/V1/restaurantes/{id}/garcons` com nome, email, telefone e senha.
2. Sistema valida campos (nome ≥ 3, email válido, telefone 10–15, senha ≥ 6).
3. Sistema verifica que o email não está duplicado.
4. Sistema cria o usuário com `tipo = GARCOM` e senha hasheada com BCrypt.
5. Sistema vincula o garçom ao restaurante (`restauranteTrabalho`).
6. Sistema retorna 201 Created com id, nome, email, telefone e tipo=GARCOM.

**Fluxos Alternativos:**
- **1a.** Usuário autenticado não é dono → 403 Forbidden.
- **3a.** Email já cadastrado → 400 Bad Request "Este e-mail já está cadastrado."
- **2a.** Validação de campos falha → 400 Bad Request.

---

### CU-04: Abrir Mesa

| Campo | Valor |
|---|---|
| **ID** | CU-04 |
| **Atores** | GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Usuário autenticado vinculado ao restaurante |
| **Pós-condições** | Mesa criada com status LIVRE |
| **RFs** | RF001 |
| **Spec** | spec/mesas |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/mesas` com restauranteId e nomeCliente.
2. Sistema valida que restauranteId pertence ao restaurante do usuário.
3. Sistema valida nomeCliente não vazio.
4. Sistema cria a mesa com `status = LIVRE` e dataAbertura = agora.
5. Sistema retorna 201 Created com id, nomeCliente, status, dataAbertura, restauranteId, restauranteNome, itensCompartilhados=[].

**Fluxos Alternativos:**
- **2a.** RestauranteId não pertence ao restaurante do usuário → 404 Not Found.

---

### CU-05: Criar Comanda

| Campo | Valor |
|---|---|
| **ID** | CU-05 |
| **Atores** | GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Mesa existe e pertence ao restaurante |
| **Pós-condições** | Comanda criada com status ABERTA; mesa pode ficar OCUPADA |
| **RFs** | RF010 |
| **Spec** | spec/comandas |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/mesas/{mesaId}/comandas` com clienteNome e opcionalmente itens.
2. Sistema valida que a mesa pertence ao restaurante.
3. Sistema valida clienteNome obrigatório.
4. Sistema cria comanda com `status = ABERTA`.
5. Para cada item: copia precoUnitario do produto (snapshot), calcula subtotal.
6. Sistema calcula valorTotal = soma(precoUnitario × quantidade).
7. Se mesa era LIVRE → muda para OCUPADA.
8. Sistema retorna 201 Created com id, clienteNome, status, valorTotal, itens.

**Fluxos Alternativos:**
- **2a.** Mesa não encontrada ou de outro restaurante → 404 Not Found.
- **3a.** clienteNome vazio → 400 Bad Request.
- **3b.** Item com produtoId inexistente ou quantidade ≤ 0 → 400 Bad Request.

---

### CU-06: Adicionar Item na Comanda

| Campo | Valor |
|---|---|
| **ID** | CU-06 |
| **Atores** | GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Comanda existe, está ABERTA e pertence ao restaurante |
| **Pós-condições** | Item adicionado; valorTotal atualizado |
| **RFs** | RF013 |
| **Spec** | spec/comandas |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/comandas/{id}/itens` com produtoId e quantidade.
2. Sistema valida que comanda está ABERTA.
3. Sistema copia precoUnitario do produto (snapshot).
4. Sistema adiciona item à comanda.
5. Sistema atualiza valorTotal da comanda.
6. Sistema retorna 200 OK com id do item, produtoId, quantidade, precoUnitario.

**Fluxos Alternativos:**
- **2a.** Comanda não está ABERTA → 400 Bad Request.
- **2b.** Produto não encontrado → 404 Not Found.

---

### CU-07: Ratear Item Compartilhado

| Campo | Valor |
|---|---|
| **ID** | CU-07 |
| **Atores** | GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Comanda ABERTA; item compartilhado existe na mesa |
| **Pós-condições** | Rateio registrado; saldo do item reduzido |
| **RFs** | RF016 |
| **Spec** | spec/comandas |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/comandas/{id}/rateio` com produtoId e valorPago.
2. Sistema valida que comanda está ABERTA.
3. Sistema valida que produto está no pool de itens compartilhados da mesa.
4. Sistema calcula saldo restante = (quantidade × precoUnitario) - soma(rateios existentes).
5. Sistema valida que valorPago > 0 e ≤ saldo restante.
6. Sistema cria ComandaRateio com valorPago e dataPagamento = agora.
7. Sistema retorna 200 OK com comanda atualizada.

**Fluxos Alternativos:**
- **5a.** valorPago excede saldo → 400 Bad Request com saldo disponível.
- **3a.** Produto não está no pool da mesa → 400 Bad Request.

---

### CU-08: Fechar Comanda (Presencial)

| Campo | Valor |
|---|---|
| **ID** | CU-08 |
| **Atores** | GARCOM, DONO_RESTAURANTE |
| **Pré-condições** | Comanda existe e está ABERTA |
| **Pós-condições** | Comanda → PAGA; mesa pode voltar para LIVRE |
| **RFs** | RF017, RF023 |
| **Spec** | spec/pix |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/comandas/{id}/fechar` com `{"formaPagamento": "DINHEIRO|CARTAO_CREDITO|MAQUININHA"}`.
2. Sistema valida que formaPagamento não é PIX (usa endpoint próprio).
3. Sistema muda status da comanda para PAGA.
4. Sistema registra dataFechamento.
5. Se esta é a última comanda PAGA/CANCELADA da mesa → mesa muda para LIVRE.
6. Sistema retorna 200 OK com comanda atualizada.

**Fluxos Alternativos:**
- **2a.** formaPagamento = PIX → 400 Bad Request "Use o endpoint /pix para gerar QR Code."
- **1a.** Comanda não está ABERTA → 400 Bad Request.
- **2b.** formaPagamento nulo ou inválido → 422 Unprocessable Entity.

---

### CU-09: Gerar QR Code PIX

| Campo | Valor |
|---|---|
| **ID** | CU-09 |
| **Atores** | GARCOM, DONO_RESTAURANTE, Mercado Pago |
| **Pré-condições** | Comanda ABERTA ou AGUARDANDO_PIX |
| **Pós-condições** | QR Code gerado; comanda → AGUARDANDO_PIX |
| **RFs** | RF019, RF020 |
| **Spec** | spec/pix |

**Fluxo Principal:**
1. Garçom envia POST `/API/V1/comandas/{id}/pix`.
2. Sistema valida que comanda está ABERTA ou AGUARDANDO_PIX.
3. Se ABERTA: calcula valor = valorTotal - soma(rateios).
4. Se AGUARDANDO_PIX com transação PENDENTE: retorna QR existente (idempotente).
5. Se AGUARDANDO_PIX com transação EXPIRADA: gera nova cobrança.
6. Sistema resolve token do restaurante (conexão própria ou fallback global).
7. Sistema cria TransacaoPix com status PENDENTE.
8. Sistema muda comanda para AGUARDANDO_PIX.
9. Sistema retorna 200 OK com qrCodeBase64, payloadCopiaCola, txId.

**Fluxos Alternativos:**
- **2a.** Comanda PAGA → 400 Bad Request "Comanda já está paga."
- **3a.** Total já rateado → 400 Bad Request "Comanda já foi totalmente paga via rateio."

---

### CU-10: Encerrar Mesa

| Campo | Valor |
|---|---|
| **ID** | CU-10 |
| **Atores** | DONO_RESTAURANTE |
| **Pré-condições** | Mesa existe e pertence ao restaurante |
| **Pós-condições** | Comandas abertas → CANCELADA; mesa → LIVRE |
| **RFs** | RF010 |
| **Spec** | spec/mesas, spec/comandas |

**Fluxo Principal:**
1. Dono envia POST `/API/V1/mesas/{id}/encerrar`.
2. Sistema localiza a mesa.
3. Sistema identifica comandas com status ABERTA ou AGUARDANDO_PIX.
4. Sistema muda essas comandas para CANCELADA.
5. Sistema preserva todos os dados históricos (itens, valores, rateios).
6. Sistema muda status da mesa para LIVRE.
7. Sistema retorna 200 OK com mesa atualizada.

**Fluxos Alternativos:**
- **3a.** Nenhuma comanda aberta → 200 OK direto (mesa → LIVRE).
- **1a.** Mesa não encontrada → 404 Not Found.
- **1b.** Usuário não é dono do restaurante → 403 Forbidden.

---

## Resumo

| ID | Caso de Uso | Atores Principais | RFs |
|---|---|---|---|
| CU-01 | Cadastrar Usuário | Visitante | RF046 |
| CU-02 | Login | Todos os usuários | RF039, RF042 |
| CU-03 | Criar Garçom | DONO_RESTAURANTE | RF045 |
| CU-04 | Abrir Mesa | GARCOM, DONO | RF001 |
| CU-05 | Criar Comanda | GARCOM, DONO | RF010 |
| CU-06 | Adicionar Item na Comanda | GARCOM, DONO | RF013 |
| CU-07 | Ratear Item Compartilhado | GARCOM, DONO | RF016 |
| CU-08 | Fechar Comanda (Presencial) | GARCOM, DONO | RF017, RF023 |
| CU-09 | Gerar QR Code PIX | GARCOM, DONO, MP | RF019, RF020 |
| CU-10 | Encerrar Mesa | DONO_RESTAURANTE | RF010 |

---

## Rastreabilidade

- **Histórias de usuário:** `Documentaçao/historias-usuario/index.md`
- **Requisitos funcionais:** `Documentaçao/requesitos/backend/`
- **Specs OpenSpec:** `openspec/specs/`
- **Diagrama UML:** `diagrama-casos-de-uso.puml` (gera `diagrama-casos-de-uso-v2.png`)
