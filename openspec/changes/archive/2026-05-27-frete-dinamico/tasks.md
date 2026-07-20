## 1. Entidade e Modelo

- [x] 1.1 Criar a entidade `RegiaoEntrega` no pacote `models/` com campos: `id`, `nome`, `valorFrete`, `restaurante` (ManyToOne)
- [x] 1.2 Adicionar relacionamento `@OneToMany` em `Restaurante` para `RegiaoEntrega`
- [x] 1.3 Criar o repositório `RegiaoEntregaRepository` no pacote `repositories/`
- [x] 1.4 Rodar a aplicação com H2 e verificar se a tabela `regiao_entrega` é criada

## 2. DTOs

- [x] 2.1 Criar `RegiaoEntregaRequestDTO` com `nome` (@NotBlank) e `valorFrete` (@NotNull @PositiveOrZero)
- [x] 2.2 Criar `RegiaoEntregaResponseDTO` com `id`, `nome`, `valorFrete`

## 3. Service e Controller de Regiões

- [x] 3.1 Criar `RegiaoEntregaService` com métodos: `listar`, `buscarPorId`, `criar`, `atualizar`, `deletar`
- [x] 3.2 Validar no service que o restaurante existe antes de operar
- [x] 3.3 Criar `RegiaoEntregaController` com endpoints REST aninhados em `/API/V1/restaurantes/{restauranteId}/regioes`
- [x] 3.4 Registrar as novas exceptions no `ResourceExceptionHandler` se necessário

## 4. Alterar Cálculo do Frete no Pedido

- [x] 4.1 Adicionar campo `regiaoEntregaId` ao `PedidoRequestDTO` (nullable)
- [x] 4.2 Injetar `RegiaoEntregaRepository` no `PedidoService`
- [x] 4.3 Substituir a linha `BigDecimal taxaEntrega = new BigDecimal("5.00")` pela lógica que busca a região e obtém `valorFrete`
- [x] 4.4 Validar que a região pertence ao restaurante do pedido; caso contrário lançar `ValidacaoNegocioException`
- [x] 4.5 Atualizar `DataLoader` para cadastrar regiões de entrega nos restaurantes seed

## 5. Testes e Verificação

- [x] 5.1 Compilar o projeto com `mvnw compile` e corrigir erros
- [x] 5.2 Subir a aplicação com `mvnw spring-boot:run` (profile h2) e testar os novos endpoints via Swagger
- [x] 5.3 Verificar que um pedido criado via POST retorna `taxaEntrega` com o valor da região escolhida
- [x] 5.4 Verificar que pedido com região inválida retorna erro 400/404