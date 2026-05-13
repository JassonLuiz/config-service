# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Project Overview

This repository is part of a **distributed configuration management system** composed of two modules:

- **`config-service`** (this module): Central REST API server that stores and publishes configurations.
- **`client-library`** (sibling at `../client-library`): Spring library consumed by client applications to fetch and cache configs.

---

## Commands

### Build & Run (config-service)

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run locally
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ConfigEntryServiceTest

# Run a single test method
mvn test -Dtest=ConfigEntryServiceTest#shouldCreateConfig
```

### Infrastructure (Docker)

```bash
# Start all dependencies (PostgreSQL, Kafka, Zookeeper, Redis, Kafdrop)
docker-compose up -d

# Stop all
docker-compose down
```

Services exposed:
- PostgreSQL: `localhost:5432` (db: `configdb`, user: `postgres`, pass: `postgres`)
- Kafka: `localhost:9092`
- Redis: `localhost:6379`
- Kafdrop (Kafka UI): `http://localhost:9000`
- config-service API: `http://localhost:8080`

### Authentication

All API endpoints require HTTP Basic Auth: `admin` / `admin123` (except `/actuator/**`).

---

## Architecture

### Domain Model

```
Namespace (key: unique string)
  └── Environment (key: unique string, scoped per namespace)
        └── ConfigEntry (key, value, description)
```

All entities use UUID primary keys and have `createdAt`/`updatedAt` managed by `@PrePersist`/`@PreUpdate`.

### REST API — URL Pattern

```
/api/v1/namespace                                               → Namespace CRUD
/api/v1/namespaces/{namespaceKey}/environments                  → Environment CRUD
/api/v1/namespaces/{namespaceKey}/environments/{envKey}/configs → ConfigEntry CRUD
/api/v1/namespace/{namespace}/env/{env}/sync-force              → Force full resync
```

Config creation accepts a **batch** payload (`List<ConfigEntryCreateDTO>`).

### Event Flow (Kafka)

When a config is created, updated, or deleted:

1. `ConfigEntryService` calls `KafkaNotificationService`
2. `KafkaNotificationService` delegates to `KafkaProducerService`
3. `KafkaProducerService` dynamically creates (if needed) and publishes to topic: `config.{namespace}.{environment}`
4. Each event is a `ConfigEvent` record containing `EventType` (CREATE/UPDATE/DELETE), namespace, environment, list of config keys, correlationId, and timestamp
5. Client applications subscribed to that Kafka topic receive the event, fetch updated values from the REST API, and refresh their Redis cache + in-memory beans

### Correlation ID / Distributed Tracing

`CorrelationIdFilter` runs on every request:
- Reads `X-Correlation-ID` header (or generates a UUID)
- Stores it in `MDC` so it appears in all log lines
- Echoes it back in the response header

### Exception Handling

`GlobalExceptionHandler` maps domain exceptions to HTTP status codes:
- `ConfigAlreadyExistsException` → 409
- `ConfigNotFoundException` / `EnvironmentNotFoundException` → 404
- `IllegalArgumentException` / `MethodArgumentNotValidException` → 400
- Any other `Exception` → 500

### Validation

`ConfigEntryValidator` runs before persistence and:
1. Validates each DTO via JSR-303 (`@NotBlank`, etc.)
2. Checks for duplicate keys within the same request batch
3. Checks for duplicate keys already in the database for that environment

---

## client-library Integration

Client applications add the library as a dependency, annotate their main class with `@EnableConfigClient`, and configure:

```properties
config.namespace=my-app
config.environment=production
config.api.base-url=http://localhost:8080
config.api.username=admin
config.api.password=admin123
config.client.kafka.enabled=true
config.client.kafka.topics=config.my-app.production
config.client.kafka.group-id=my-app-group
```

Fields annotated with `@ConfigValue(key="some.key")` are injected at startup via `ConfigValueInjector` (a `BeanPostProcessor`) and refreshed automatically when a Kafka event arrives.

**Caching chain in the client:**
1. `DynamicConfigHolder` — in-memory `ConcurrentHashMap` (L1)
2. Redis — shared cache with 30-day TTL, key format `config:{namespace}:{environment}:{key}` (L2)
3. Config-service REST API — fallback with up to 3 retries (10s delay) (L3)

---

## Key Design Notes

- **Kafka topics are created dynamically** by `KafkaProducerService` at publish time using `AdminClient` — no pre-provisioning needed.
- **`SyncService.forceSync()`** is a stub; the Kafka publish call is commented out and currently only returns `true`.
- **`ConfigClient.getString()`** deserializes the Redis value as a `ConfigEntryResponseDTO` JSON, with a plain-string fallback for non-JSON values.
- The client library's `ConfigValueInjector` only supports `String` field types; other types (`Boolean`, `Integer`) are handled explicitly by `ConfigClient.getBoolean()` / `ConfigClient.getInt()`.
- Security uses in-memory `UserDetailsService` — not suitable for production as-is.
