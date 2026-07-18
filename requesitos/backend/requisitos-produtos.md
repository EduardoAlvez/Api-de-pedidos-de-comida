# Requisitos — Produtos

## RF025: Criar Produto
- **Endpoint:** `POST /API/V1/produtos`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Body:** `{ "nome": String, "descricao": String, "preco": BigDecimal, "precoMeia": BigDecimal (opcional), "categoria": String, "imageUrl": String (opcional), "disponivel": boolean (default true) }`
- **Resposta:** `201 Created` com `ProdutoResponseDTO`
- **Validações:**
  - `nome` e `preco` obrigatórios
  - Produto vinculado ao restaurante do usuário logado

## RF026: Listar Produtos do Restaurante
- **Endpoint:** `GET /API/V1/produtos/restaurante/{restauranteId}`
- **Autenticação:** JWT (qualquer role)
- **Resposta:** `200 OK` com `List<ProdutoResponseDTO>`
- **Validações:**
  - Garçons/donos: só veem produtos do próprio restaurante
  - Clientes: podem ver produtos de qualquer restaurante

## RF027: Buscar Produto por ID
- **Endpoint:** `GET /API/V1/produtos/{id}`
- **Autenticação:** Nenhuma (público)
- **Resposta:** `200 OK` com `ProdutoResponseDTO`

## RF028: Atualizar Produto
- **Endpoint:** `PUT /API/V1/produtos/{id}`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Resposta:** `200 OK` com `ProdutoResponseDTO`

## RF029: Deletar Produto
- **Endpoint:** `DELETE /API/V1/produtos/{id}`
- **Autenticação:** JWT (role DONO_RESTAURANTE)
- **Resposta:** `204 No Content`

## RF030: Meia Porção
- Produto pode ter `precoMeia` opcional (nullable)
- Se `precoMeia` preenchido → produto oferece meia porção
- Se `precoMeia = null` → produto só é vendido como inteira
- Ao criar item do pedido com `tamanho = MEIA`:
  - Usa `produto.precoMeia` para calcular subtotal
  - Se `precoMeia` for null → `400 Bad Request` "Produto não oferece meia porção"

## RF031: Categorias
- Produto possui campo `categoria` (String livre)
- Usado para agrupar produtos no cardápio (ex: "Bebida", "Prato Principal", "Sobremesa")
