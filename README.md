# Sistema de Configuração Distribuída

Monorepo Maven com a API de configurações, os contratos compartilhados, o SDK cliente e uma aplicação demonstrativa.

## Módulos

| Módulo | Descrição | Porta |
|---|---|---|
| `config-contracts` | Contratos compartilhados entre API e SDK (`ConfigEvent`, `EventType`, `ConfigEntryResponseDTO`) | — |
| `config-service` | API central de configurações (REST + JPA + Security + Kafka + DLQ) | 8080 |
| `client-library` | SDK cliente (`@ConfigValue`, cache local + Redis, consumidor Kafka) | — |
| `springexample1` | Aplicação demonstrativa que consome o SDK | 8085 |

Todos os módulos usam **Java 21** e **Spring Boot 3.5.1**, definidos no POM agregador da raiz.

## Build

```bash
./mvnw verify          # build + testes de todos os módulos
./mvnw checkstyle:check
./mvnw -pl config-service -am package   # apenas a API e suas dependências
```

## Execução com Docker

```bash
cp .env.example .env   # opcional: ajuste credenciais, portas e KAFKA_ADVERTISED_HOST
docker compose up --build
```

Sobe PostgreSQL, ZooKeeper, Kafka, Redis, Kafdrop (http://localhost:9000), a API (http://localhost:8080) e a aplicação demonstrativa (http://localhost:8085).

Para acessar o Kafka de fora do Docker a partir de outra máquina, defina `KAFKA_ADVERTISED_HOST` com o IP do host no `.env`.

## Roteiro da demonstração

```bash
# 1. Criar namespace e ambiente
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/namespace \
  -H 'Content-Type: application/json' -d '{"key":"my-app","description":"App de exemplo"}'

curl -u admin:admin123 -X POST http://localhost:8080/api/v1/namespaces/my-app/environments \
  -H 'Content-Type: application/json' -d '{"key":"dev","description":"Desenvolvimento"}'

# 2. Criar configurações
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/namespaces/my-app/environments/dev/configs \
  -H 'Content-Type: application/json' \
  -d '[{"key":"store-name","value":"Loja Exemplo","description":"Nome da loja"}]'

# 3. Observar a aplicação demonstrativa aplicando a configuração
curl http://localhost:8085/api/test/config-status
```

## Variáveis de ambiente

| Variável | Módulo | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | `config-service` | PostgreSQL local |
| `KAFKA_BOOTSTRAP_SERVERS` | `config-service`, `springexample1` | `localhost:9092` |
| `CONFIG_ADMIN_USERNAME` / `CONFIG_ADMIN_PASSWORD` | `config-service` | `admin` / `admin123` |
| `CONFIG_API_BASE_URL` / `CONFIG_API_USERNAME` / `CONFIG_API_PASSWORD` | `springexample1` | API local |
| `CONFIG_NAMESPACE` / `CONFIG_ENVIRONMENT` | `springexample1` | `my-app` / `dev` |
| `REDIS_HOST` / `REDIS_PORT` | `springexample1` | `localhost` / `6379` |

As credenciais padrão servem apenas para desenvolvimento local; defina-as por variável de ambiente em qualquer outro cenário.
