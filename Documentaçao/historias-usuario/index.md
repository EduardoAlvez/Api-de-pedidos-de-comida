# Histórias de Usuario — Comanda Digital

> **Escopo:** Backend presencial (mesas, comandas, garçons, pagamentos).
> **Fontes:** Requisitos funcionais RF001–RF050 + specs OpenSpec.
> **Convenção:** Cada história segue o formato *Como [ator], quero [ação], para [benefício]*.

---

## Épico 1 — Autenticação e Segurança

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-01 | **Como** CLIENTE, **quero** me cadastrar com nome, email, telefone e senha, **para** usar o sistema. | Cadastro retorna 201 com `tipo: CLIENTE`. Campo `tipo` no payload é ignorado. Senha hasheada com BCrypt. | RF046 | spec/usuarios |
| HU-02 | **Como** usuário, **quero** fazer login com email e senha, **para** receber um token JWT válido por 2 horas. | Credenciais inválidas retornam 401. Token contém email como subject. | RF039, RF042 | spec/usuarios, spec/autenticacao-garcom |
| HU-03 | **Como** usuário autenticado, **quero** que o PUT/DELETE da minha conta verifique meu ID, **para** que ninguém altere minha conta. | PUT/DELETE com ID diferente do token retornam 400. | RF047 | spec/usuarios |
| HU-04 | **Como** DONO_RESTAURANTE, **quero** que o cadastro público nunca crie garçons ou donos, **para** manter a segurança do sistema. | Tipo é sempre forçado para CLIENTE no cadastro público. | RF046 | spec/usuarios |

---

## Épico 2 — Garçons e Vínculo

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-05 | **Como** DONO_RESTAURANTE, **quero** criar um garçom com nome, email, telefone e senha, **para** que ele possa atender mesas do meu restaurante. | Retorna 201 com `tipo: GARCOM`. Garçom vinculado automaticamente ao restaurante. Senha hasheada com BCrypt. | RF045 | spec/garcons |
| HU-06 | **Como** DONO_RESTAURANTE, **quero** vincular um garçom existente ao meu restaurante, **para** reutilizar contas já cadastradas. | Retorna 200. Garçom não pode estar vinculado a outro restaurante. Usuário deve ter role GARCOM. | RF044 | spec/garcons |
| HU-07 | **Como** DONO_RESTAURANTE, **quero** desvincular um garçom do meu restaurante, **para** encerrar a relação quando necessário. | Retorna 204. Garçom deve estar vinculado ao restaurante. | RF049 | spec/garcons |
| HU-08 | **Como** DONO_RESTAURANTE, **quero** listar todos os garçons vinculados ao meu restaurante, **para** visualizar minha equipe. | Retorna 200 com lista de id, nome, email, tipo. | RF050 | spec/garcons |

---

## Épico 3 — Mesas

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-09 | **Como** GARCOM, **quero** abrir uma mesa informando o nome do cliente, **para** iniciar o atendimento presencial. | Retorna 201 com `status: LIVRE`. | RF001 | spec/mesas |
| HU-10 | **Como** GARCOM, **quero** listar as mesas do restaurante, **para** verificar quais estão livres ou ocupadas. | Retorna 200 com lista. Filtra automaticamente pelo restaurante do usuário. | RF002, RF009 | spec/mesas |
| HU-11 | **Como** DONO_RESTAURANTE, **quero** encerrar uma mesa, **para** cancelar comandas abertas e liberar a mesa. | Comandas ABERTA/AGUARDANDO_PIX → CANCELADA. Mesa volta para LIVRE. Histórico preservado. | RF010 | spec/mesas, spec/comandas |
| HU-12 | **Como** GARCOM, **quero** deletar uma mesa, **para** removê-la do sistema. | Só permitido sem comandas ABERTA/AGUARDANDO_PIX. Comandas CANCELADA não bloqueiam. Retorna 204. | RF005 | spec/mesas |

---

## Épico 4 — Comandas e Itens

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-13 | **Como** GARCOM, **quero** criar uma comanda para um cliente na mesa, **para** registrar seus pedidos. | Retorna 201 com `status: ABERTA`. Se mesa era LIVRE → muda para OCUPADA. `valorTotal` calculado automaticamente. | RF010 | spec/comandas |
| HU-14 | **Como** GARCOM, **quero** adicionar um item na comanda, **para** registrar o pedido do cliente. | Retorna 200. `precoUnitario` copiado do produto (snapshot). `valorTotal` atualizado. Comanda deve estar ABERTA. | RF013 | spec/comandas |
| HU-15 | **Como** GARCOM, **quero** atualizar a quantidade de um item na comanda, **para** corrigir erros de registro. | Retorna 200. Comanda deve estar ABERTA. `valorTotal` recalculado. | RF014 | spec/comandas |
| HU-16 | **Como** GARCOM, **quero** remover um item da comanda, **para** cancelar um pedido incorreto. | Retorna 204. Comanda deve estar ABERTA. `valorTotal` recalculado. | RF015 | spec/comandas |
| HU-17 | **Como** GARCOM, **quero** adicionar itens compartilhados à mesa, **para** registrar pedidos que serão rateados. | Retorna 201. Item fica vinculado à mesa, não a comanda específica. `precoUnitario` é snapshot. | RF006 | spec/mesas |

---

## Épico 5 — Rateio

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-18 | **Como** GARCOM, **quero** registrar o rateio de um item compartilhado em uma comanda, **para** dividir contas entre clientes. | Retorna 200. `valorPago` não pode exceder saldo restante. Saldo = (quantidade × precoUnitario) - soma(rateios). Comanda não fecha automaticamente. | RF016 | spec/comandas |

---

## Épico 6 — Pagamento

| ID | História | Critérios de Aceite | RFs | Specs |
|---|---|---|---|---|
| HU-19 | **Como** GARCOM, **quero** fechar uma comanda com pagamento presencial (DINHEIRO, CARTAO, MAQUININHA), **para** confirmar o pagamento. | Retorna 200 com `status: PAGA`. Se última comanda da mesa → mesa volta para LIVRE. | RF017, RF023 | spec/pix |
| HU-20 | **Como** GARCOM, **quero** gerar QR Code PIX para uma comanda, **para** que o cliente pague digitalmente. | Retorna 200 com qrCodeBase64 e payloadCopiaCola. Comanda → AGUARDANDO_PIX. Transação criada com status PENDENTE. | RF019 | spec/pix |
| HU-21 | **Como** GARCOM, **quero** consultar o status de uma transação PIX, **para** verificar se o pagamento foi confirmado. | Retorna 200 com status e QR Base64 (se AGUARDANDO). Comanda sem transação → 404. | RF022 | spec/pix |
| HU-22 | **Como** GARCOM, **quero** regenerar QR Code quando o anterior expirou, **para** que o cliente possa pagar. | Se transação EXPIRADA → nova cobrança. Se PENDENTE → retorna QR existente (idempotente). | RF019 | spec/pix |

---

## Resumo por Épico

| Épico | Qtd | IDs |
|---|---|---|
| Autenticação e Segurança | 4 | HU-01 a HU-04 |
| Garçons e Vínculo | 4 | HU-05 a HU-08 |
| Mesas | 4 | HU-09 a HU-12 |
| Comandas e Itens | 5 | HU-13 a HU-17 |
| Rateio | 1 | HU-18 |
| Pagamento | 4 | HU-19 a HU-22 |
| **Total** | **22** | |

---

## Rastreabilidade

Cada história é rastreável a:
- **Requisitos funcionais (RF):** listados na coluna RFs da tabela.
- **Specs OpenSpec:** listados na coluna Specs da tabela.
- **Diagrama de casos de uso:** `Documentaçao/casos-de-uso/diagrama-casos-de-uso.puml`
