## 1. Infraestrutura (Fly.io)

- [ ] 1.1 Criar volume: `flyctl volumes create uploads --region ord --size 1`
- [ ] 1.2 Adicionar no `fly.toml`:
  ```toml
  [[mounts]]
    source = "uploads"
    destination = "/data/uploads"
  ```

## 2. Configuração Spring

- [ ] 2.1 Criar `WebConfig.java` com `addResourceHandlers` apontando `/uploads/**` para `file:/data/uploads/`
- [ ] 2.2 Configurar `spring.servlet.multipart.max-file-size=5MB` no `application-postgre.properties`

## 3. DTOs

- [ ] 3.1 Adicionar `imageUrl` (opcional) em `ProdutoRequestDTO`
- [ ] 3.2 Adicionar `imageUrl` (opcional) em `RestauranteRequestDTO`

## 4. Controllers

- [ ] 4.1 Adicionar em `ProdutoController`:
  - `PUT /API/V1/produtos/{id}/imagem` → upload multipart
- [ ] 4.2 Adicionar em `RestauranteController`:
  - `PUT /API/V1/restaurantes/{id}/imagem` → upload multipart

## 5. Services

- [ ] 5.1 Criar `FileStorageService.java` com métodos:
  - `salvarImagem(Long entidadeId, String prefixo, MultipartFile arquivo)` → retorna path
  - `deletarImagem(String path)`
- [ ] 5.2 Atualizar `ProdutoService.criar()` para copiar `imageUrl` do DTO
- [ ] 5.3 Atualizar `RestauranteService.criar()` para copiar `imageUrl` do DTO

## 6. DataLoader

- [ ] 6.1 Baixar imagens gratuitas para cada produto do seed (Unsplash)
- [ ] 6.2 Copiar imagens para `/data/uploads/produtos/` no volume
- [ ] 6.3 Atualizar `DataLoader` para usar paths locais: `/uploads/produtos/feijoada.jpg`

## 7. Testes

- [ ] 7.1 Testar upload de imagem com multipart
- [ ] 7.2 Testar tamanho máximo excedido → 400
- [ ] 7.3 Testar servir imagem estatica → 200
- [ ] 7.4 Testar imagem não encontrada → 404

## 8. Verificação

- [ ] 8.1 Rodar `mvn compile`
- [ ] 8.2 Rodar `mvn test`
- [ ] 8.3 Deploy no Fly.io e testar upload real
