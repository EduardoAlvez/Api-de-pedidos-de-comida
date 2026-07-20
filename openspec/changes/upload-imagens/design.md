## Context

Atualmente `Produto.imageUrl` e `Restaurante.imageUrl` são strings que só o DataLoader preenche com URLs placeholder. DTOs de request não aceitam o campo. Não há upload de arquivos nem serving estático.

## Goals / Non-Goals

**Goals:**
- Dono pode fazer upload de imagem para produtos e restaurante
- Imagens servidas estaticamente pelo próprio backend
- DTOs aceitam `imageUrl` (URL externa opcional)
- Seed inicial com imagens reais baixadas da internet
- Volumes Fly.io para persistência

**Non-Goals:**
- Não criar CDN ou cloud storage externo (volume é suficiente)
- Não redimensionar/otimizar imagens (para já)
- Não fazer cache layer (para já)

## Decisions

1. **Fly Volume vs. Cloud Storage vs. Base64**
   → Fly Volume. É grátis (3GB inclusos), simples de configurar, e o suficiente para centenas de imagens de produto.

2. **ResourceHandler vs. Controller dedicado**
   → `WebMvcConfigurer.addResourceHandlers()`. Zero código, Spring serve direto do disco. O controller só existe para upload.

3. **Salvar em /data/uploads/produtos/{id}.{ext} vs. /data/uploads/{hash}.{ext}**
   → `/data/uploads/produtos/{id}.{ext}`. Mais simples de debugar, e o ID do produto já é único.

4. **Upload substitui vs. versiona**
   → Substitui. Se dono faz upload de nova imagem, o arquivo antigo é sobrescrito. Sem versionamento por enquanto.

5. **Imagens de seed (DataLoader)**
   → Baixar do Unsplash (fotos gratuitas, licença free) usando IDs conhecidos. Ou baixar manualmente e colocar no repositório.

## Risks

- Volume existe em apenas uma região. Se a máquina for movida, o volume não acompanha.
- Sem backup automático do volume.
- Tamanho máximo do volume: 1GB comporta ~2000 imagens em boa qualidade.
