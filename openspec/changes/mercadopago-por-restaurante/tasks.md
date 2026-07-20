## 1. Modelos e Entidades

- [ ] 1.1 Adicionar campos `mpAccessToken` (String, nullable) e `mpWebhookSecret` (String, nullable) em `Restaurante.java`
- [ ] 1.2 Criar migration SQL para adicionar colunas na tabela `restaurantes` (se não estiver usando ddl-auto=update)

## 2. DTOs

- [ ] 2.1 Criar `MercadoPagoConfigDTO.java` com campos `accessToken` e `webhookSecret`

## 3. Controller

- [ ] 3.1 Adicionar em `RestauranteController`:
  - `PUT /API/V1/restaurantes/{id}/mercado-pago` → 200 (só DONO)
  - `DELETE /API/V1/restaurantes/{id}/mercado-pago` → 204
- [ ] 3.2 Alterar em `PixController`:
  - Rota do webhook de `/pix/webhook` para `/pix/webhook/{restauranteId}`

## 4. Services

- [ ] 4.1 Remover `MercadoPagoConfigSetup.java`
- [ ] 4.2 Alterar `MercadoPagoPixClient.criarCobranca(valor, descricao, accessToken)` — token como parâmetro
- [ ] 4.3 Alterar `PixService`:
  - `gerarQrCode(comandaId)`: obter token de `comanda.mesa.restaurante.mpAccessToken`
  - Validar se token existe → se não, 400
  - `processarWebhook(restauranteId, payload, assinatura)`: obter secret via `restauranteRepository`
- [ ] 4.4 Alterar `ComandaService.fechar()`: quando `formaPagamento == PIX`, verificar token do restaurante

## 5. DataLoader

- [ ] 5.1 Opcional: setar token dummy no restaurante de exemplo para testes locais

## 6. Testes

- [ ] 6.1 Atualizar `PixControllerTest` para usar nova rota de webhook com `restauranteId`
- [ ] 6.2 Atualizar `PixServiceTest` para mockar token do restaurante
- [ ] 6.3 Adicionar teste: gerar PIX sem token → 400
- [ ] 6.4 Adicionar teste: configurar credenciais como dono → 200
- [ ] 6.5 Adicionar teste: configurar credenciais como não-dono → 403

## 7. Verificação

- [ ] 7.1 Rodar `mvn compile` para verificar compilação
- [ ] 7.2 Rodar `mvn test` para verificar todos os testes
