## ADDED Requirements

### Requirement: Dono pode fazer upload de imagem do produto
O sistema SHALL permitir que o dono envie uma imagem para um produto.
- Endpoint: `PUT /API/V1/produtos/{id}/imagem`
- Autenticação: JWT obrigatório (role DONO_RESTAURANTE)
- Content-Type: `multipart/form-data`
- Body: campo `imagem` (arquivo de imagem)
- Formatos aceitos: JPEG, PNG, WebP
- Tamanho máximo: 5MB
- Salva em: `/data/uploads/produtos/{id}.{ext}`
- Atualiza: `produto.imageUrl = /uploads/produtos/{id}.{ext}`
- Retorna: `200 OK` com `ProdutoResponseDTO`
- Se arquivo for maior que 5MB → `400 Bad Request`

#### Scenario: Dono faz upload de imagem com sucesso
- **WHEN** dono autenticado envia PUT para `/API/V1/produtos/1/imagem` com arquivo JPEG válido
- **THEN** sistema retorna `200 OK` e `produto.imageUrl` contém `/uploads/produtos/1.jpg`

#### Scenario: Não-dono tenta fazer upload
- **WHEN** garçom tenta fazer upload de imagem
- **THEN** sistema retorna `403 Forbidden`

#### Scenario: Upload de arquivo maior que 5MB
- **WHEN** dono envia arquivo de 10MB
- **THEN** sistema retorna `400 Bad Request`

### Requirement: Dono pode fazer upload de imagem do restaurante
Mesma lógica, endpoint: `PUT /API/V1/restaurantes/{id}/imagem`
- Salva em: `/data/uploads/restaurantes/{id}.{ext}`
- Atualiza: `restaurante.imageUrl = /uploads/restaurantes/{id}.{ext}`

### Requirement: Imagens servidas estaticamente
O sistema SHALL servir os arquivos de `/data/uploads/` no path `/uploads/`.
- `GET /uploads/produtos/1.jpg` → retorna o arquivo
- `GET /uploads/restaurantes/1.jpg` → retorna o arquivo
- Se arquivo não existir → `404 Not Found`

### Requirement: ProdutoRequestDTO aceita imageUrl
O sistema SHALL permitir que o dono defina uma URL externa ao criar/atualizar produto.
- `ProdutoRequestDTO` ganha campo `imageUrl` opcional
- Se preenchido, o valor é salvo diretamente no banco
- Tem precedência sobre upload (se ambos forem enviados, o último vence)

#### Scenario: Criar produto com URL de imagem externa
- **WHEN** dono cria produto via POST `/API/V1/produtos` com `imageUrl: "https://cdn.exemplo.com/pizza.jpg"`
- **THEN** sistema salva a URL e retorna no response
