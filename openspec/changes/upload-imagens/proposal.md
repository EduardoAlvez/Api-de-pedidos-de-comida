## Why

Produtos e restaurantes possuem campo `imageUrl` no banco, mas não há como o dono cadastrar imagens. O DataLoader popula com URLs fictícias (`https://exemplo.com/feijoada.jpg`). O frontend (app Android e futura web) precisa exibir imagens reais dos produtos e do restaurante.

## What Changes

- **Criar volume Fly.io** `uploads` (1GB, grátis) montado em `/data/uploads`
- **Criar endpoint de upload** `PUT /API/V1/produtos/{id}/imagem` (multipart) e `PUT /API/V1/restaurantes/{id}/imagem`
- **Servir imagens estaticamente** via `ResourceHandler` em `/uploads/**`
- **Adicionar `imageUrl`** nos `ProdutoRequestDTO` e `RestauranteRequestDTO` (para aceitar URL externa também)
- **Seed de imagens**: baixar fotos gratuitas (Unsplash/Pexels) e popular no DataLoader
- **Dono pode trocar imagem** depois via endpoint de upload

### Fluxo esperado

1. DataLoader: `imageUrl = "/uploads/produtos/feijoada.jpg"` (servido estaticamente)
2. Dono pode sobrescrever via API: `PUT /API/V1/produtos/{id}/imagem` com multipart
3. Dono também pode passar URL externa: `PUT /API/V1/produtos/{id}` com `imageUrl: "https://..."`

## Capabilities

- `upload-imagens`: Upload e servir imagens de produtos e restaurantes

## Impact

- **Infra**: Volume Fly.io + config mounts no fly.toml
- **Config**: Spring ResourceHandler para servir arquivos estaticamente
- **Models**: `ProdutoRequestDTO` e `RestauranteRequestDTO` ganham campo `imageUrl` opcional
- **Controllers**: `ProdutoController.uploadImagem()` e `RestauranteController.uploadImagem()`
- **Services**: Lógica de salvar arquivo em disco
- **DataLoader**: Imagens reais baixadas para seed data
