# Matriz de Rastreabilidade — Comanda Digital

## Legenda

| Tipo | Significado |
|------|-------------|
| **Direta** | Elemento UML corresponde 1:1 a um arquivo/componente de código |
| **Parcial** | Elemento UML corresponde parcialmente, com adaptações |
| **Indireta** | Elemento UML não tem correspondência direta, mas está representado indiretamente |
| **Ausente** | Elemento UML não foi implementado |

---

## 1. Casos de Uso

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| UC1 | Abrir Mesa | `MainActivity.java` → `NewMesaDialog.java` | Card "+" na lista, dialog "Nova Mesa" | Direta |
| UC2 | Lançar Item na Comanda | `ComandaActivity.java` → `LaunchForClientDialog.java` → `CardapioActivity.java` | Botão "+", dialog "Lançar para", tela Cardápio | Direta |
| UC3 | Editar Item | `ComandaAdapter.java:151` → `EditItemDialog.java` | Ícone de editar no item, dialog "Editar Item" | Direta |
| UC4 | Excluir Item | `ComandaAdapter.java:156` → `DeleteItemDialog.java` | Ícone de lixeira no item, dialog "Excluir Item" | Direta |
| UC5 | Fechar Conta | `ComandaActivity.java:136` → `FecharContaDialog.java` | Botão "Fechar Conta", dialog de método de pagamento | Direta |
| UC6 | Cancelar Mesa | `MesaAdapter.java` → `DeleteMesaDialog.java` | Ícone de lixeira no card da mesa, dialog "Cancelar Comanda" | Direta |
| UC7 | Pagar Presencial | `FecharContaDialog.java` → `PagamentoPresencialDialog.java` | Dialog "Pagamento Presencial" com seleção de forma | Direta |
| UC8 | Pagar PIX | `FecharContaDialog.java` → `PixDialog.java` | Dialog "PIX" com QR Code e código copia-e-cola | Direta |
| UC9 | Rateio | `FecharContaDialog.java` → `RateioDialog.java` | Dialog "Rateio" com seleção de itens e valores | Direta |
| UC10 | Adicionar Novo Cliente | `LaunchForClientDialog.java` → `NewClientDialog.java` | Opção "Adicionar Novo Cliente" na lista, dialog "Novo Cliente" | Direta |

---

## 2. Diagrama de Classes

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| CL1 | `Mesa` (id, numero, nomeCliente, status, dataAbertura, totalPedido) | `model/Mesa.java` | Card de mesa na RecyclerView (`item_table_occupied.xml`) | Direta |
| CL2 | `ItemComanda` (type, productName, preco, qtd, isShared, pessoasDividido, valorPago, productNote) | `model/ItemComanda.java` | Itens na RecyclerView da comanda (`item_order_product.xml`) | Direta |
| CL3 | `Comanda` (mesaId, itens, total) | `controller/ComandaActivity.java` (lista `itens` como campo) | Tela de comanda completa | Parcial |
| CL4 | `Produto` (id, nome, preco, categoria, imageUrl) | `model/Produto.java` + `model/ProdutoResponse.java` | Cards de produto no cardápio (`item_menu_product.xml`) | Direta |
| CL5 | `CartItem` (produto, quantidade, observacao) | `model/CartItem.java` | Barra flutuante de pedido (`layout_order_float_bar.xml`) | Direta |
| CL6 | `ClientOption` (id, name, role, type) | `model/ClientOption.java` | Linhas no dialog "Lançar para" (`item_client_row.xml`) | Direta |
| CL7 | `LoginRequest` / `LoginResponse` | `model/LoginRequest.java` / `model/LoginResponse.java` | Tela de login (`activity_login.xml`) | Direta |
| CL8 | `MesaResponse` / `RestauranteResponse` | `model/MesaResponse.java` / `model/RestauranteResponse.java` | (dados da API — sem UI direta) | Indireta |

---

## 3. Atributos Principais

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| AT1 | Mesa.numero | `Mesa.getNumero()` | `tvTableNumberDecor` + `chipTableNumber` | Direta |
| AT2 | Mesa.nomeCliente | `Mesa.getNomeCliente()` | `tvMesaName` no header, `tvClientName` no card | Direta |
| AT3 | Mesa.totalPedido | `ItemComanda.getTotalPedido()` (armazenado no header) | `tvTotalAmount` no item `TYPE_MESA_INFO` | Direta |
| AT4 | ItemComanda.productName | `ItemComanda.getProductName()` | `tvProductName` | Direta |
| AT5 | ItemComanda.preco | `ItemComanda.getPreco()` | `tvPrice` | Direta |
| AT6 | ItemComanda.preco × qtd | `item.getPreco() * item.getQtd()` | `tvPrice` (após correção) | Direta |
| AT7 | ItemComanda.qtd | `ItemComanda.getQtd()` | `tvQuantity` (`QTD: %02d`) | Direta |
| AT8 | ItemComanda.isShared | `ItemComanda.isShared()` | `viewSidebar` (vermelho individual, azul compartilhado) | Direta |
| AT9 | ItemComanda.productNote | `ItemComanda.getProductNote()` | `tvProductNote` | Direta |
| AT10 | ItemComanda.pessoasDividido | `ItemComanda.getPessoasDividido()` | `tvPriceDetail` ("R$ XX/pessoa") | Direta |
| AT11 | ItemComanda.dividedChip | `ItemComanda.getDividedChip()` | `tvDividedChip` ("DIVIDIR EM 4") | Direta |
| AT12 | Produto.nome | `Produto.getName()` / `ProdutoResponse.getNome()` | `tvProductName` no cardápio | Direta |
| AT13 | Produto.preco | `Produto.getPrice()` / `ProdutoResponse.getPreco()` | `tvPrice` no cardápio | Direta |
| AT14 | Produto.categoria | `ProdutoResponse.getCategoria()` | Abas de categoria no `CardapioAdapter` | Parcial |
| AT15 | CartItem.quantidade | `CartItem.getQuantidade()` | Contador no float bar | Direta |
| AT16 | ClientOption.name | `ClientOption.getName()` | `tvClientName` no dialog | Direta |
| AT17 | ClientOption.role | `ClientOption.getRole()` | `tvClientRole` no dialog | Direta |

---

## 4. Mensagens de Sequência

### Fluxo 1: Abrir Mesa

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| SQ1 | Garçom → clica "+" | `MesaAdapter.java` → `setOnAddTableListener` | Card "+" no final da lista | Direta |
| SQ2 | MainActivity → NewMesaDialog | `MainActivity.java` → `NewMesaDialog.show()` | Dialog centralizado "Nova Mesa" | Direta |
| SQ3 | NewMesaDialog → API | `NewMesaDialog.java:92` → `btnAbrirMesa` | Botão "Abrir Mesa", campos número e nome | Direta |
| SQ4 | API → MesaAdapter | `callback.onMesaCriada(mesa)` → `adapter.notifyItemInserted()` | Card da nova mesa na lista | Direta |

### Fluxo 2: Lançar Item

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| SQ5 | Garçom → clica "+" | `ComandaActivity.java:237` → `btnAddItem` | Botão "+" na barra inferior | Direta |
| SQ6 | ComandaActivity → LaunchForClientDialog | `ComandaActivity.java:246` → `dialog.show()` | Dialog "Lançar para" com lista de clientes | Direta |
| SQ7 | Garçom → seleciona cliente | `LaunchForClientDialog.java:71` → `adapter` | Cliente selecionado na lista | Direta |
| SQ8 | LaunchForClientDialog → CardapioActivity | `ComandaActivity.java:250` → `startActivityForResult()` | Tela do cardápio | Direta |
| SQ9 | CardapioActivity → ReviewOrderActivity | `CardapioActivity.java` → `ReviewOrderActivity` | Tela de revisão do pedido | Parcial |
| SQ10 | ReviewOrderActivity → ComandaActivity | `ComandaActivity.java:270` → `onActivityResult()` | Itens inseridos na comanda | Direta |
| SQ11 | ComandaActivity → ComandaAdapter | `adapter.notifyDataSetChanged()` | RecyclerView atualizada | Direta |

### Fluxo 3: Fechar Conta

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| SQ12 | Garçom → clica "Fechar Conta" | `ComandaActivity.java:136` → `btnFecharConta` | Botão "Fechar Conta" na barra inferior | Direta |
| SQ13 | ComandaActivity → FecharContaDialog | `ComandaActivity.java:140` → `dialog.show()` | Dialog "Método de Pagamento" | Direta |
| SQ14 | FecharContaDialog → PagamentoPresencialDialog | `FecharContaDialog.java:141` | Dialog "Pagamento Presencial" | Direta |
| SQ15 | FecharContaDialog → PixDialog | `FecharContaDialog.java:162` | Dialog "PIX" com QR Code | Direta |
| SQ16 | FecharContaDialog → RateioDialog | `FecharContaDialog.java:189` | Dialog "Rateio" | Direta |
| SQ17 | Confirmado → ComandaActivity | `onPagamentoConfirmado()` / `onPixConfirmado()` / `onRateioConfirmado()` | Toast de confirmação | Direta |
| SQ18 | ComandaActivity → itens | `marcarTodosPagos()` / `marcarPagosDaPessoa()` | Itens marcados como PAGO | Direta |

---

## 5. Requisitos Não Funcionais

| ID | Elemento UML | Artefato no Código | Componente de Interface | Correspondência |
|----|--------------|---------------------|------------------------|----------------|
| RNF1 | Plataforma Mobile | `build.gradle.kts` (minSdk 24, targetSdk 36) | Aplicativo Android nativo | Direta |
| RNF2 | Usabilidade (3 toques para lançar) | `ComandaActivity.java` → `CardapioActivity.java` (2 telas) | Fluxo: "+" → seleciona cliente → cardápio | Ausente (não verificado) |
| RNF3 | Responsividade | Layouts com `LinearLayout`, `FrameLayout`, `CardView` | Adaptação a diferentes telas | Parcial |
| RNF4 | Desempenho (busca < 2s) | (depende da API backend) | Campo de busca no cardápio | Ausente |
| RNF5 | Disponibilidade (99.9%) | (infraestrutura do servidor) | N/A | Ausente |
| RNF6 | Segurança (autenticação JWT) | `LoginActivity.java` + `ApiClient.java` + `ApiService.java` | Tela de login, token em SharedPreferences | Direta |
| RNF7 | Integridade (cache local) | (não implementado) | N/A | Ausente |
| RNF8 | Concorrência (múltiplos terminais) | (não implementado) | N/A | Ausente |
| RNF9 | Manutenibilidade (backend independente) | `service/ApiClient.java` (Retrofit) + `service/ApiService.java` | Dados fornecidos pela API REST | Direta |
| RNF10 | Interface Visual | Tema vermelho/laranja em `colors.xml`, `styles.xml` | Cores, fontes (`Work Sans`, `Inter`), estilos Material | Direta |

---

## 6. Documentação de Requisitos (Novos Documentos)

*Referências: `Documentaçao/historias-usuario/index.md`, `Documentaçao/casos-de-uso/index.md`*

| ID | Documento | Conteúdo | RFs Cobertos | Status |
|----|-----------|----------|--------------|--------|
| DOC1 | Histórias de Usuário | 22 user stories em 6 épicos (Autenticação, Garçons, Mesas, Comandas, Rateio, Pagamento) | RF001–RF050 | Atualizado |
| DOC2 | Casos de Uso Backend | 10 casos de uso detalhados (CU-01 a CU-10) com fluxos principais e alternativos | RF001–RF050 | Atualizado |
| DOC3 | Diagrama de Casos de Uso | UML com 5 atores, 6 pacotes, relações `<<include>>` e `<<extends>>` | — | Gerado |

---

## Resumo Quantitativo

| Categoria | Total | Direta | Parcial | Indireta | Ausente | % Rastreado |
|-----------|-------|--------|---------|----------|---------|-------------|
| Casos de Uso | 10 | 10 | 0 | 0 | 0 | 100% |
| Classes | 8 | 6 | 1 | 1 | 0 | 87,5% |
| Atributos | 17 | 16 | 1 | 0 | 0 | 94,1% |
| Mensagens Sequência | 18 | 17 | 1 | 0 | 0 | 94,4% |
| Requisitos Não Funcionais | 10 | 4 | 1 | 0 | 5 | 40% |
| Documentação de Requisitos | 3 | 3 | 0 | 0 | 0 | 100% |
| **Total Geral** | **66** | **56** | **4** | **1** | **5** | **84,8%** |
