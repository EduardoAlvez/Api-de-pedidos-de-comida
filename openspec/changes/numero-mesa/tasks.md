## 1. Modelos

- [ ] 1.1 Adicionar campo `numero` (Integer, nullable) em `Mesa.java`

## 2. DTOs

- [ ] 2.1 Adicionar `numero` (opcional) em `MesaRequestDTO`
- [ ] 2.2 Adicionar `numero` em `MesaResponseDTO`

## 3. Repository

- [ ] 3.1 Adicionar método em `MesaRepository`:
  - `Optional<Mesa> findByRestaurante_IdAndNumero(Long restauranteId, Integer numero)`

## 4. Service

- [ ] 4.1 Atualizar `MesaService.criar()`:
  - Se `numero` informado, validar unicidade no restaurante
  - Lançar `ValidacaoNegocioException` se duplicado

## 5. Testes

- [ ] 5.1 Testar criar mesa com número
- [ ] 5.2 Testar criar mesa sem número
- [ ] 5.3 Testar número duplicado → 400
- [ ] 5.4 Testar mesmo número em restaurantes diferentes → 200

## 6. Verificação

- [ ] 6.1 Rodar `mvn compile`
- [ ] 6.2 Rodar `mvn test`
