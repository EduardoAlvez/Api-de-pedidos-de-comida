## 1. Role GARCOM e Autenticação

- [x] 1.1 Adicionar `GARCOM` no enum `Role` com `getRole()` retornando `ROLE_GARCOM`
- [x] 1.2 Atualizar `Usuario.getAuthorities()`: GARCOM recebe `ROLE_GARCOM` + `ROLE_CLIENTE`
- [x] 1.3 Atualizar `DataLoader` com um usuário garçom de exemplo

## 2. Campo `origem` no Pedido

- [x] 2.1 Criar enum `OrigemPedido` com valores `DELIVERY` e `PRESENCIAL`
- [x] 2.2 Adicionar campo `origem` na entidade `Pedido` (nullable, default DELIVERY)
- [x] 2.3 Atualizar `PedidoResponseDTO` para incluir `origem`
- [x] 2.4 Garantir que pedidos delivery existentes continuem funcionando

## 3. Entidade Mesa

- [x] 3.1 Criar entidade `Mesa`: id, nomeCliente, status (enum), dataAbertura, restaurante (ManyToOne)
- [x] 3.2 Criar enum `StatusMesa` com LIVRE e OCUPADA
- [x] 3.3 Criar `MesaRequestDTO` e `MesaResponseDTO`
- [x] 3.4 Criar `MesaRepository`
- [x] 3.5 Criar `MesaService` com métodos: criar, listar, buscarPorId, atualizar, deletar
- [x] 3.6 Criar `MesaController` com endpoints

## 4. Entidade Comanda

- [x] 4.1 Criar entidade `Comanda`: id, mesa (ManyToOne), garcom (ManyToOne), clienteNome, status (enum), dataAbertura, dataFechamento, valorTotal
- [x] 4.2 Criar enum `StatusComanda` com ABERTA, FECHADA, PAGA
- [x] 4.3 Criar entidade `ComandaItem`: id, comanda (ManyToOne), produto (ManyToOne), quantidade, precoUnitario, compartilhado (boolean)
- [x] 4.4 Criar `ComandaRequestDTO`, `ComandaResponseDTO`, `ComandaItemDTO`
- [x] 4.5 Criar `ComandaRepository` e `ComandaItemRepository`
- [x] 4.6 Criar `ComandaService` com métodos: criar, listarPorMesa, buscarPorId, adicionarItem, removerItem, fechar
- [x] 4.7 Criar `ComandaController` com endpoints aninhados em mesa

## 5. Rateio de Itens Compartilhados

- [x] 5.1 Criar entidade `ComandaRateio`: id, comanda (ManyToOne), produto (ManyToOne), valorPago, dataPagamento
- [x] 5.2 Criar `RateioRequestDTO` com produtoId e valorPago
- [x] 5.3 Criar `ComandaRateioRepository`
- [x] 5.4 Implementar lógica de rateio no `ComandaService`: validar que valorPago não excede saldo pendente
- [x] 5.5 Criar endpoint `POST /API/V1/comandas/{id}/rateio`

## 6. Fechamento e Liberação de Mesa

- [x] 6.1 Implementar lógica de fechamento de comanda: status → PAGA, dataFechamento
- [x] 6.2 Ao fechar comanda, verificar se é a última ABERTA/FECHADA → liberar mesa (status → LIVRE)
- [x] 6.3 Atualizar `DataLoader` com mesas e comandas de exemplo

## 7. Testes e Verificação

- [x] 7.1 Compilar o projeto com `mvnw compile` e corrigir erros
- [x] 7.2 Subir aplicação com profile h2 (teste contextLoads passou)
- [x] 7.3 Testar fluxo completo: login garçom → abrir mesa → criar comandas → adicionar itens → rateio → fechar ✅
- [x] 7.4 Verificar que delivery existente continua funcionando ✅
