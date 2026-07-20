## ADDED Requirements

### Requirement: Role GARCOM
O sistema SHALL adicionar o papel `GARCOM` no enum `Role`. Garçons recebem a autoridade `ROLE_GARCOM` e podem acessar endpoints de mesa e comanda.

#### Scenario: Usuario com role GARCOM faz login
- **WHEN** um usuário com tipo `GARCOM` envia POST para `/login`
- **THEN** o sistema retorna token JWT e `getAuthorities()` contém `ROLE_GARCOM`

#### Scenario: Usuario GARCOM não tem acesso de DONO_RESTAURANTE
- **WHEN** um garçom tenta acessar `/API/V1/restaurantes` (PUT/DELETE)
- **THEN** o sistema retorna 403 Forbidden (se houver restrição por role)
