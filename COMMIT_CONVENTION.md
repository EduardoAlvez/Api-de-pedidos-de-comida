# Commit Message

## Mensagem Sugerida

```
FEATURE: Frete dinâmico por região de entrega

- Cria entidade RegiaoEntrega vinculada ao Restaurante
- Adiciona CRUD de regiões de entrega (/API/V1/restaurantes/{id}/regioes)
- Remove taxa de entrega fixa (R$ 5,00) e implementa cálculo dinâmico
- PedidoRequestDTO agora aceita regiaoEntregaId para cálculo do frete
- Atualiza DataLoader com 5 regiões de entrega de exemplo
- Adiciona RegiaoEntregaNaoEncontradaException
```

## Padrão de Commits do Projeto

Com base no histórico, o projeto segue este formato:

```
TIPO: Descrição concisa do que foi feito

- Item opcional com detalhe específico
- Outro detalhe
```

### Tipos usados no projeto

| Tipo | Quando usar |
|------|-------------|
| `FEATURE` | Nova funcionalidade |
| `REFATORAÇÃO` | Mudança na estrutura sem alterar comportamento |
| `CORREÇÕES` / `CORREÇÃO` | Correção de bugs |
| `MUDANÇA` / `Mudanças` | Alterações gerais (evitar, preferir tipo específico) |

### Recomendação (Conventional Commits adaptado)

Para consistência futura, sugere-se padronizar em português:

- `FEATURE: descrição` — nova funcionalidade
- `CORREÇÃO: descrição` — correção de bug
- `REFATORAÇÃO: descrição` — refatoração
- `DOCS: descrição` — documentação
- `TESTES: descrição` — testes
- `CONFIG: descrição` — configuração/infra

O corpo da mensagem (opcional) lista os principais arquivos ou mudanças em bullet points.
