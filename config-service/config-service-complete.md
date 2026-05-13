# Config Service - Documentação Completa

Gerado automaticamente do projeto

## 📁 Estrutura de Pastas

```
├── config-service/
│   ├── docker-compose.yml
│   ├── pom.xml
│   ├── logs/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── io/
│   │   │   │   │   ├── github/
│   │   │   │   │   │   ├── configservice/
│   │   │   │   │   │   │   ├── config_service/
│   │   │   │   │   │   │   │   ├── ConfigServiceApplication.java
│   │   │   │   │   │   │   │   ├── config/
│   │   │   │   │   │   │   │   │   ├── CorrelationIdFilter.java
│   │   │   │   │   │   │   │   │   ├── KafkaConfig.java
│   │   │   │   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   │   │   │   ├── ValidationConfig.java
│   │   │   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   │   │   │   ├── ConfigEntryController.java
│   │   │   │   │   │   │   │   │   ├── EnvironmentController.java
│   │   │   │   │   │   │   │   │   ├── NamespaceController.java
│   │   │   │   │   │   │   │   │   ├── SyncController.java
│   │   │   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   │   │   ├── ConfigEntryBatchDTO.java
│   │   │   │   │   │   │   │   │   ├── createDTO/
│   │   │   │   │   │   │   │   │   │   ├── ConfigEntryCreateDTO.java
│   │   │   │   │   │   │   │   │   │   ├── EnvironmentCreateDTO.java
│   │   │   │   │   │   │   │   │   │   ├── NamespaceCreateDTO.java
│   │   │   │   │   │   │   │   │   ├── responseDTO/
│   │   │   │   │   │   │   │   │   │   ├── ConfigEntryResponseDTO.java
│   │   │   │   │   │   │   │   │   │   ├── EnvironmentResponseDTO.java
│   │   │   │   │   │   │   │   │   │   ├── NamespaceResponseDTO.java
│   │   │   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   │   │   ├── ConfigEvent.java
│   │   │   │   │   │   │   │   │   ├── EventType.java
│   │   │   │   │   │   │   │   ├── exception/
│   │   │   │   │   │   │   │   │   ├── ConfigAlreadyExistsException.java
│   │   │   │   │   │   │   │   │   ├── ConfigNotFoundException.java
│   │   │   │   │   │   │   │   │   ├── EnvironmentNotFoundException.java
│   │   │   │   │   │   │   │   │   ├── ErrorResponse.java
│   │   │   │   │   │   │   │   │   ├── ValidationErrorResponse.java
│   │   │   │   │   │   │   │   │   ├── handler/
│   │   │   │   │   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   │   │   ├── ConfigEntry.java
│   │   │   │   │   │   │   │   │   ├── Environment.java
│   │   │   │   │   │   │   │   │   ├── Namespace.java
│   │   │   │   │   │   │   │   │   ├── mapper/
│   │   │   │   │   │   │   │   │   │   ├── ConfigEntryMapper.java
│   │   │   │   │   │   │   │   ├── producer/
│   │   │   │   │   │   │   │   │   ├── KafkaProducer.java
│   │   │   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   │   │   ├── ConfigEntryRepository.java
│   │   │   │   │   │   │   │   │   ├── EnvironmentRepository.java
│   │   │   │   │   │   │   │   │   ├── NamespaceRepository.java
│   │   │   │   │   │   │   │   ├── service/
│   │   │   │   │   │   │   │   │   ├── ConfigEntryService.java
│   │   │   │   │   │   │   │   │   ├── EnvironmentService.java
│   │   │   │   │   │   │   │   │   ├── KafkaNotificationService.java
│   │   │   │   │   │   │   │   │   ├── NamespaceService.java
│   │   │   │   │   │   │   │   │   ├── SyncService.java
│   │   │   │   │   │   │   │   │   ├── validator/
│   │   │   │   │   │   │   │   │   │   ├── ConfigEntryValidator.java
│   │   │   ├── resources/
│   │   │   │   ├── application.properties
│   │   │   │   ├── logback-spring.xml
│   │   ├── test/
│   │   │   ├── java/
│   │   │   │   ├── io/
│   │   │   │   │   ├── github/
│   │   │   │   │   │   ├── configservice/
│   │   │   │   │   │   │   ├── config_service/
│   │   │   │   │   │   │   │   ├── ConfigServiceApplicationTests.java
│   │   │   ├── resources/
│   │   │   │   ├── application.properties
```

## 📄 Código Fonte

### Configuração

#### `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.1</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>io.github.configservice</groupId>
	<artifactId>config-service</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>config-service</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>

		<dependency>
			<groupId>net.logstash.logback</groupId>
			<artifactId>logstash-logback-encoder</artifactId>
			<version>7.4</version>
		</dependency>

		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

#### `src\main\resources\application.properties`

```properties
spring.application.name=config-service

#ConfigEntry for server
server.port=8080

#ConfigEntry for database
spring.datasource.url=jdbc:postgresql://localhost:5432/configdb
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

#ConfigEntry for kafka
kafka.topics.config-updates="config-update-topic"
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:192.168.0.106:9092}

#ConfigEntry for logging
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%X{correlationId}] %-5level %logger{36} - %msg%n


```

#### `src\test\resources\application.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Desabilita Kafka nos testes
spring.kafka.bootstrap-servers=localhost:9092
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
```

---

### Models/Entities

#### `src\main\java\io\github\configservice\config_service\model\ConfigEntry.java`

```java
package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ConfigEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String key;
    private String value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    public ConfigEntry() {}

    public ConfigEntry(UUID id, String key, String value, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Environment environment) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.environment = environment;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

#### `src\main\java\io\github\configservice\config_service\model\Environment.java`

```java
package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Environment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String key;
    private String description;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfigEntry> configs = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Environment() {}

    public Environment(UUID id, String key, String descricao, Namespace namespace) {
        this.id = id;
        this.key = key;
        this.description = descricao;
        this.namespace = namespace;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public List<ConfigEntry> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigEntry> configs) {
        this.configs = configs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

#### `src\main\java\io\github\configservice\config_service\model\Namespace.java`

```java
package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Namespace {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String key;
    private String description;

    @OneToMany(mappedBy = "namespace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Environment> environments = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Namespace() {}

    public Namespace(UUID id, String key, String descricao) {
        this.id = id;
        this.key = key;
        this.description = descricao;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

#### `src\main\java\io\github\configservice\config_service\model\mapper\ConfigEntryMapper.java`

```java
package io.github.configservice.config_service.model.mapper;

import io.github.configservice.config_service.dto.responseDTO.ConfigEntryResponseDTO;
import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigEntryMapper {

    public ConfigEntryResponseDTO toResponseDTO(ConfigEntry configEntry) {
        if (configEntry == null) {
            return null;
        }

        return new ConfigEntryResponseDTO(
                configEntry.getKey(),
                configEntry.getValue(),
                configEntry.getDescription(),
                configEntry.getCreatedAt(),
                configEntry.getUpdatedAt()
        );
    }

    public List<ConfigEntryResponseDTO> toResponseDTOList(List<ConfigEntry> configEntries) {
        if (configEntries == null) {
            return List.of();
        }

        return configEntries.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
```

---

### Repositories

#### `src\main\java\io\github\configservice\config_service\repository\ConfigEntryRepository.java`

```java
package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfigEntryRepository extends JpaRepository<ConfigEntry, UUID> {
    Optional<ConfigEntry> findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(String Key, String envKey, String namespaceKey);
    List<ConfigEntry> findAllByEnvironment_KeyAndEnvironment_Namespace_Key(String envKey, String namespaceKey);
    boolean existsByEnvironment_KeyAndEnvironment_Namespace_Key(String envKey, String namespaceKey);
    Optional<ConfigEntry> existsByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(String key, String envKey, String namespaceKey);
}
```

#### `src\main\java\io\github\configservice\config_service\repository\EnvironmentRepository.java`

```java
package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    Optional<Environment> findByKeyAndNamespace_Key(String key, String namespaceKey);
    List<Environment> findByNamespace_Key(String namespaceKey);
    List<Environment> findAllByNamespace_Key(String namespaceKey);
    Optional<Environment> existsByKeyAndNamespace_Key(String key, String namespaceKey);
}
```

#### `src\main\java\io\github\configservice\config_service\repository\NamespaceRepository.java`

```java
package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NamespaceRepository extends JpaRepository<Namespace, UUID> {
    Optional<Namespace> findByKey(String key);
    Optional<Namespace> existsByKey(String key);
}
```

---

### Services

#### `docker-compose.yml`

```yaml
version: "3.8"

services:
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: configdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    networks:
      - config-net
    volumes:
      - postgres_data:/var/lib/postgresql/data

  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: config-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - config-net

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: config-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.0.106:9092,DOCKER://kafka:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,DOCKER:PLAINTEXT
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,DOCKER://0.0.0.0:29092
      KAFKA_INTER_BROKER_LISTENER_NAME: DOCKER
      KAFKA_TOPIC_CREATE_ATTEMPTS: 5
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    healthcheck:
      test: [ "CMD", "kafka-topics", "--bootstrap-server", "192.168.0.106:9092", "--list" ]
      interval: 10s
      timeout: 5s
      retries: 10
    networks:
      - config-net

  redis:
    image: redis:7
    container_name: config-redis
    ports:
      - "6379:6379"
    networks:
      - config-net

  kafdrop:
    image: obsidiandynamics/kafdrop
    container_name: config-kafdrop
    depends_on:
      - kafka
    ports:
      - "9000:9000"
    environment:
      KAFKA_BROKER_CONNECT: kafka:29092
      SERVER_PORT: 9000
    networks:
      - config-net
    restart: always

volumes:
  postgres_data:

networks:
  config-net:

```

#### `src\main\java\io\github\configservice\config_service\ConfigServiceApplication.java`

```java
package io.github.configservice.config_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServiceApplication.class, args);
	}

}
```

#### `src\main\java\io\github\configservice\config_service\config\CorrelationIdFilter.java`

```java
package io.github.configservice.config_service.config;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;


        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);

        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated a new correlationId: {}", correlationId);
        } else {
            log.debug("Using existing correlationId: {}", correlationId);
        }

        MDC.put(CORRELATION_ID, correlationId);

        httpResponse.addHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
```

#### `src\main\java\io\github\configservice\config_service\config\KafkaConfig.java`

```java
package io.github.configservice.config_service.config;

import io.github.configservice.config_service.event.ConfigEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ConfigEvent> producerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10485760);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ConfigEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        configs.put("request.timeout.ms", 30000);
        configs.put("connections.max.idle.ms", 300000);
        return new KafkaAdmin(configs);
    }
}
```

#### `src\main\java\io\github\configservice\config_service\config\SecurityConfig.java`

```java
package io.github.configservice.config_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(PasswordEncoder encoder){
        UserDetails user = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
```

#### `src\main\java\io\github\configservice\config_service\config\ValidationConfig.java`

```java
package io.github.configservice.config_service.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
```

#### `src\main\java\io\github\configservice\config_service\controller\ConfigEntryController.java`

```java
package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.ConfigEntryBatchDTO;
import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.ConfigEntryResponseDTO;
import io.github.configservice.config_service.service.ConfigEntryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespaces/{namespaceKey}/environments/{envKey}/configs")
public class ConfigEntryController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final ConfigEntryService configEntryService;

    public ConfigEntryController(ConfigEntryService configEntryService) {
        this.configEntryService = configEntryService;
    }

    @GetMapping
    public ResponseEntity<List<ConfigEntryResponseDTO>> findAll(@PathVariable String namespaceKey,
                                                                @PathVariable String envKey) {
        return ResponseEntity.ok(configEntryService.getAllByNamespaceAndEnvironment(namespaceKey, envKey));
    }

    @GetMapping("/{configKey}")
    public ResponseEntity<ConfigEntryResponseDTO> findByKey(@PathVariable String namespaceKey,
                                                            @PathVariable String envKey,
                                                            @PathVariable String configKey) {
        return configEntryService.getByKey(namespaceKey, envKey, configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<List<ConfigEntryResponseDTO>> create(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @Valid @RequestBody List<ConfigEntryCreateDTO> dto) {
        log.info("Creating config with key='{}' for namespace='{}', environment='{}'",
                dto.size(), namespaceKey, envKey);
        List<ConfigEntryResponseDTO> created = configEntryService.createConfiguration(namespaceKey, envKey, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{configKey}")
    public ResponseEntity<ConfigEntryResponseDTO> update(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @PathVariable String configKey,
                                                         @Valid @RequestBody ConfigEntryCreateDTO dto) {
        log.info("Updating config with key='{}' for namespace='{}', environment='{}'",
                configKey, namespaceKey, envKey);
        return configEntryService.updateConfiguration(namespaceKey, envKey, configKey, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{configKey}")
    public ResponseEntity<Void> delete(@PathVariable String namespaceKey,
                                       @PathVariable String envKey,
                                       @PathVariable String configKey) {
        boolean deleted = configEntryService.deleteConfiguration(namespaceKey, envKey, configKey);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
```

#### `src\main\java\io\github\configservice\config_service\controller\EnvironmentController.java`

```java
package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.createDTO.EnvironmentCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.EnvironmentResponseDTO;
import io.github.configservice.config_service.service.EnvironmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespaces/{namespaceKey}/environments")
public class EnvironmentController {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentController.class);
    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping
    public ResponseEntity<List<EnvironmentResponseDTO>> findAllByNamespace(@PathVariable String namespaceKey) {
        return ResponseEntity.ok(environmentService.findAllByNamespace(namespaceKey));
    }

    @GetMapping("/{key}")
    public ResponseEntity<EnvironmentResponseDTO> findByKey(@PathVariable String namespaceKey,
                                                            @PathVariable String envKey) {
        return environmentService.findByKeyAndNamespace(envKey, namespaceKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EnvironmentResponseDTO> create(@PathVariable String namespaceKey,
                                                         @Valid @RequestBody EnvironmentCreateDTO dto) {
        log.info("Creating environment with key='{}' for namespace='{}'", dto.key(), namespaceKey);
        EnvironmentResponseDTO created = environmentService.createEnvironment(dto, namespaceKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{envKey}")
    public ResponseEntity<EnvironmentResponseDTO> update(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @Valid @RequestBody EnvironmentCreateDTO dto) {
        log.info("Updating environment with key='{}' for namespace='{}'", envKey, namespaceKey);
        return environmentService.update(namespaceKey, envKey, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    //Add metodo delete
}
```

#### `src\main\java\io\github\configservice\config_service\controller\NamespaceController.java`

```java
package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.createDTO.NamespaceCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.NamespaceResponseDTO;
import io.github.configservice.config_service.service.NamespaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespace")
public class NamespaceController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final NamespaceService namespaceService;

    public NamespaceController(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @GetMapping
    public ResponseEntity<List<NamespaceResponseDTO>> findAll() {
        return ResponseEntity.ok(namespaceService.findAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<NamespaceResponseDTO> findByKey(@PathVariable String key) {
        return namespaceService.findByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NamespaceResponseDTO> create(@RequestBody NamespaceCreateDTO dto) {
        log.info("Creating namespace with key='{}'", dto.key());
        NamespaceResponseDTO created = namespaceService.createNamespace(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

#### `src\main\java\io\github\configservice\config_service\controller\SyncController.java`

```java
package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/namespace/{namespace}/env/{env}")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sync-force")
    public ResponseEntity<Void> syncForce(@PathVariable String namespace,
                                          @PathVariable String env) {
        boolean triggered = syncService.forceSync(namespace, env);
        return triggered
                ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();
    }
}
```

#### `src\main\java\io\github\configservice\config_service\dto\ConfigEntryBatchDTO.java`

```java
package io.github.configservice.config_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class ConfigEntryBatchDTO {

    @NotBlank(message = "Namespace key must not be blank")
    private String key;
    private String description;
    private List<EnvironmentDTO> environments;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnvironmentDTO> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<EnvironmentDTO> environments) {
        this.environments = environments;
    }

    public static class EnvironmentDTO {
        @NotBlank(message = "Environment key must not be blank")
        private String key;
        private String description;
        private List<ConfigDTO> configs;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<ConfigDTO> getConfigs() {
            return configs;
        }

        public void setConfigs(List<ConfigDTO> configs) {
            this.configs = configs;
        }
    }

    public static class ConfigDTO {
        @NotBlank(message = "Configuration key must not be blank")
        private String key;

        @NotBlank(message = "Configuration value must not be blank")
        private String value;
        private String description;
        private LocalDateTime updatedAt;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
```

#### `src\main\java\io\github\configservice\config_service\dto\createDTO\ConfigEntryCreateDTO.java`

```java
package io.github.configservice.config_service.dto.createDTO;

import jakarta.validation.constraints.NotBlank;

public record ConfigEntryCreateDTO(@NotBlank(message = "Key is mandatory") String key, @NotBlank(message = "Value is mandatory") String value, String description) {
}
```

#### `src\main\java\io\github\configservice\config_service\dto\createDTO\EnvironmentCreateDTO.java`

```java
package io.github.configservice.config_service.dto.createDTO;

import jakarta.validation.constraints.NotBlank;

public record EnvironmentCreateDTO(@NotBlank@NotBlank(message = "Key is mandatory") String key, String description) {
}
```

#### `src\main\java\io\github\configservice\config_service\dto\createDTO\NamespaceCreateDTO.java`

```java
package io.github.configservice.config_service.dto.createDTO;

import jakarta.validation.constraints.NotBlank;

public record NamespaceCreateDTO(@NotBlank(message = "Key is mandatory") String key, String description) {
}
```

#### `src\main\java\io\github\configservice\config_service\dto\responseDTO\ConfigEntryResponseDTO.java`

```java
package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record ConfigEntryResponseDTO(String key, String value, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
```

#### `src\main\java\io\github\configservice\config_service\dto\responseDTO\EnvironmentResponseDTO.java`

```java
package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record EnvironmentResponseDTO(String key, String description, String namespaceKey,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
}
```

#### `src\main\java\io\github\configservice\config_service\dto\responseDTO\NamespaceResponseDTO.java`

```java
package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record NamespaceResponseDTO(String key, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
```

#### `src\main\java\io\github\configservice\config_service\event\ConfigEvent.java`

```java
package io.github.configservice.config_service.event;

import java.time.LocalDateTime;
import java.util.List;

public record ConfigEvent(
        EventType type,
        String namespace,
        String environment,
        List<String> configKeys,
        String correlationId,
        LocalDateTime timestamp) {
    public ConfigEvent(EventType type, String namespace, String environment, List<String> configKeys, String correlationId) {
        this(type, namespace, environment, configKeys, correlationId, LocalDateTime.now());
    }

    public ConfigEvent(EventType type, String namespace, String environment, String configKey, String correlationId) {
        this(type, namespace, environment, List.of(configKey), correlationId, LocalDateTime.now());
    }

    public int getConfigCount() {
        return configKeys != null ? configKeys.size() : 0;
    }

    public boolean hasConfigKeys() {
        return configKeys != null && !configKeys.isEmpty();
    }

    public boolean isSingleConfig() {
        return getConfigCount() == 1;
    }

    public String getSingleConfigKey() {
        if (isSingleConfig()) {
            return configKeys.get(0);
        }
        throw new IllegalStateException("Event contains multiple config keys");
    }
}
```

#### `src\main\java\io\github\configservice\config_service\event\EventType.java`

```java
package io.github.configservice.config_service.event;

public enum EventType {
    CREATE,
    UPDATE,
    DELETE,
}
```

#### `src\main\java\io\github\configservice\config_service\exception\ConfigAlreadyExistsException.java`

```java
package io.github.configservice.config_service.exception;

public class ConfigAlreadyExistsException extends RuntimeException{

    public ConfigAlreadyExistsException(String message){
        super(message);
    }
}
```

#### `src\main\java\io\github\configservice\config_service\exception\ConfigNotFoundException.java`

```java
package io.github.configservice.config_service.exception;

public class ConfigNotFoundException extends RuntimeException{
    public ConfigNotFoundException(String message) {
        super(message);
    }
}
```

#### `src\main\java\io\github\configservice\config_service\exception\EnvironmentNotFoundException.java`

```java
package io.github.configservice.config_service.exception;

public class EnvironmentNotFoundException extends RuntimeException{
    public EnvironmentNotFoundException(String message){
        super(message);
    }
}
```

#### `src\main\java\io\github\configservice\config_service\exception\ErrorResponse.java`

```java
package io.github.configservice.config_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String code,
                            String message,
                            LocalDateTime timestamp) {
}
```

#### `src\main\java\io\github\configservice\config_service\exception\ValidationErrorResponse.java`

```java
package io.github.configservice.config_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
}
```

#### `src\main\java\io\github\configservice\config_service\exception\handler\GlobalExceptionHandler.java`

```java
package io.github.configservice.config_service.exception.handler;

import io.github.configservice.config_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConfigAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConfigAlreadyExists(ConfigAlreadyExistsException ex) {
        log.warn("Configuration already exists: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "CONFLICT",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EnvironmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEnvironmentNotFound(EnvironmentNotFoundException ex) {
        log.warn("Environment not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigNotFound(ConfigNotFoundException ex){
        log.warn("Configuration not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation errors: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "VALIDATION_FAILED",
                "Validation failed for one or more fields",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
```

#### `src\main\java\io\github\configservice\config_service\producer\KafkaProducer.java`

```java
package io.github.configservice.config_service.producer;

import io.github.configservice.config_service.event.ConfigEvent;
import io.github.configservice.config_service.event.EventType;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, ConfigEvent> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, ConfigEvent> kafkaTemplate, KafkaAdmin kafkaAdmin) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdmin = kafkaAdmin;
    }

    public void publishEvent(EventType eventType, String namespace, String env, List<String> configKeys){
        if (configKeys == null || configKeys.isEmpty()) {
            logger.warn("Attempted to publish event with empty configs list");
            return;
        }

        String correlationId = getOrGenerateCorrelationId();
        ConfigEvent event = new ConfigEvent(eventType, namespace, env, configKeys, correlationId);
        String topicName = buildTopicName(namespace, env);

        ensureTopicExists(topicName);
        sendEvent(topicName, event);
    }

    public void publishEvent(EventType eventType, String namespace, String env, String configKey) {
        publishEvent(eventType, namespace, env, List.of(configKey));
    }

    private void sendEvent(String topic,ConfigEvent event) {

        kafkaTemplate.send(topic, event)
                .toCompletableFuture()
                .thenAccept(result -> {
                    if (event.isSingleConfig()) {
                        logger.info("Kafka event sent successfully. CorrelationId={}, Key={}, Offset={}, Topic={}",
                                event.correlationId(),
                                event.getSingleConfigKey(),
                                result.getRecordMetadata().offset(),
                                result.getRecordMetadata().topic());
                    } else {
                        logger.info("Kafka batch event sent successfully. CorrelationId={}, ConfigCount={}, Offset={}, Topic={}",
                                event.correlationId(),
                                event.getConfigCount(),
                                result.getRecordMetadata().offset(),
                                result.getRecordMetadata().topic());
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Error sending Kafka event. EventType={}, ConfigCount={}, Keys={}",
                            event.type(), event.getConfigCount(), event.configKeys(), ex);
                    return null;
                });
    }

    private void ensureTopicExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())){
            Set<String> topics = adminClient.listTopics().names().get();
            if(!topics.contains(topicName)){
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                logger.info("Topic '{}' dynamically created.", topicName);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Error checking/creating topic '{}': {}", topicName, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private String buildTopicName(String namespace, String env) {
        return "config.%s.%s".formatted(
                namespace.replaceAll("[^a-zA-Z0-9_-]", "-"),
                env.replaceAll("[^a-zA-Z0-9_-]", "-")
        );
    }

    private String getOrGenerateCorrelationId(){
        return MDC.get("correlationId") != null ? MDC.get("correlationId") : UUID.randomUUID().toString();
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\ConfigEntryService.java`

```java
package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.ConfigEntryResponseDTO;
import io.github.configservice.config_service.event.EventType;
import io.github.configservice.config_service.exception.EnvironmentNotFoundException;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.mapper.ConfigEntryMapper;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import io.github.configservice.config_service.service.validator.ConfigEntryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigEntryService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryService.class);

    private final ConfigEntryRepository configRepo;
    private final EnvironmentRepository environmentRepository;
    private final KafkaNotificationService kafkaNotificationService;
    private final ConfigEntryValidator validator;
    private final ConfigEntryMapper mapper;

    public ConfigEntryService(ConfigEntryRepository configRepo, EnvironmentRepository environmentRepository,
                              KafkaNotificationService kafkaNotificationService, ConfigEntryValidator validator, ConfigEntryMapper mapper) {
        this.configRepo = configRepo;
        this.environmentRepository = environmentRepository;
        this.kafkaNotificationService = kafkaNotificationService;
        this.validator = validator;
        this.mapper = mapper;
    }

    public List<ConfigEntryResponseDTO> getAllByNamespaceAndEnvironment(String namespaceKey, String environmentKey) {
        log.info("Fetching all configurations for namespace='{}', environment='{}'", namespaceKey, environmentKey);

        List<ConfigEntry> configs = configRepo.findAllByEnvironment_KeyAndEnvironment_Namespace_Key(environmentKey, namespaceKey);
        return mapper.toResponseDTOList(configs);
    }

    public Optional<ConfigEntryResponseDTO> getByKey(String namespace, String env, String key) {
        log.info("Searching for configuration by key='{}' in the namespace='{}', environment='{}'", key, namespace, env);

        return configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, env, namespace)
                .map(mapper::toResponseDTO);
    }

    @Transactional
    public List<ConfigEntryResponseDTO> createConfiguration(String namespaceKey, String environmentKey, List<ConfigEntryCreateDTO> configDTOs) {
        log.info("Creating {} configurations for namespace='{}', environment='{}'",
                configDTOs.size(), namespaceKey, environmentKey);

        validator.validateCreateRequest(configDTOs);
        Environment environment = findEnvironmentOrThrow(namespaceKey, environmentKey);
        validator.validateNoDuplicateKeys(configDTOs, namespaceKey, environmentKey);

        List<ConfigEntry> savedConfigs = createConfigs(configDTOs, environment);

        kafkaNotificationService.notifyConfigCreation(savedConfigs, namespaceKey, environmentKey);

        log.info("Successfully created {} configurations for namespace='{}', environment='{}'",
                savedConfigs.size(), namespaceKey, environmentKey);

        return mapper.toResponseDTOList(savedConfigs);
    }

    @Transactional
    public Optional<ConfigEntryResponseDTO> updateConfiguration(String namespaceKey, String envKey, String configKey, ConfigEntryCreateDTO dto) {
        log.info("Updating config with key='{}' for namespace='{}', environment='{}'",
                configKey, namespaceKey, envKey);

        validator.validateUpdateRequest(dto);

        return configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(configKey, envKey, namespaceKey)
                .map(existing -> {
                    updateConfigEntry(existing, dto);
                    ConfigEntry updated = configRepo.save(existing);

                    kafkaNotificationService.notifyConfigUpdate(namespaceKey, envKey, configKey);

                    log.info("Configuration updated successfully. key='{}'", configKey);
                    return mapper.toResponseDTO(updated);
                });
    }

    @Transactional
    public boolean deleteConfiguration(String namespaceKey, String envkey, String configKey) {
        log.info("Trying to remove configuration key='{}' in the namespace='{}', environment='{}'", configKey, namespaceKey, envkey);

        Optional<ConfigEntry> existing = configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(configKey, envkey, namespaceKey);

        if (existing.isPresent()){
            configRepo.delete(existing.get());
            kafkaNotificationService.notifyConfigDeletion(namespaceKey, envkey, configKey);

            log.info("Configuration removed successfully. key='{}'", configKey);
            return true;
        } else {
            log.warn("Configuration for deletion not found. key='{}'", configKey);
            return false;
        }
    }

    private Environment findEnvironmentOrThrow(String namespaceKey, String environmentKey) {
        return environmentRepository
                .findByKeyAndNamespace_Key(environmentKey, namespaceKey)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        String.format("Environment not found: key='%s', namespace='%s'", environmentKey, namespaceKey)
                ));
    }

    private List<ConfigEntry> createConfigs(List<ConfigEntryCreateDTO> configDTOs, Environment environment) {
        LocalDateTime now = LocalDateTime.now();

        return configDTOs.stream()
                .map(dto -> createSingleConfigEntry(dto, environment, now))
                .map(configRepo::save)
                .peek(config -> log.debug("Configuration created successfully: key='{}'", config.getKey()))
                .collect(Collectors.toList());
    }

    private ConfigEntry createSingleConfigEntry(ConfigEntryCreateDTO dto, Environment environment, LocalDateTime createdAt) {
        ConfigEntry configEntry = new ConfigEntry();
        configEntry.setKey(dto.key());
        configEntry.setValue(dto.value());
        configEntry.setDescription(dto.description());
        configEntry.setEnvironment(environment);
        configEntry.setCreatedAt(createdAt);
        return configEntry;
    }

    private void updateConfigEntry(ConfigEntry existing, ConfigEntryCreateDTO dto) {
        existing.setValue(dto.value());
        existing.setDescription(dto.description());
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\EnvironmentService.java`

```java
package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.responseDTO.EnvironmentResponseDTO;
import io.github.configservice.config_service.dto.createDTO.EnvironmentCreateDTO;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import io.github.configservice.config_service.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

    private final EnvironmentRepository environmentRepo;
    private final NamespaceRepository namespaceRepo;

    public EnvironmentService(EnvironmentRepository environmentRepo, NamespaceRepository namespaceRepo) {
        this.environmentRepo = environmentRepo;
        this.namespaceRepo = namespaceRepo;
    }

    public List<EnvironmentResponseDTO> findAllByNamespace(String namespaceKey) {
        return environmentRepo.findAllByNamespace_Key(namespaceKey)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<EnvironmentResponseDTO> findByKeyAndNamespace(String envKey, String namespaceKey) {
        return environmentRepo.findByKeyAndNamespace_Key(envKey, namespaceKey)
                .map(this::toResponseDTO);
    }

    public EnvironmentResponseDTO createEnvironment(EnvironmentCreateDTO dto, String namespace) {
        log.debug("Creating environment with key='{}' for namespace='{}'", dto.key(), namespace);

        //Criar exceção NamespaceNotFoundException
        Namespace namespaceEntity = namespaceRepo.findByKey(namespace)
                .orElseThrow(() -> new RuntimeException("Namespace with key=" + namespace + " not found."));

        if (environmentRepo.existsByKeyAndNamespace_Key(dto.key(), namespace).isPresent()) {
            log.info("Environment with key='{}' already exists. Reusing it.", dto.key());
            throw new RuntimeException("Environment with key=" + dto.key() + " already exists. Reusing it.");
        }

        Environment environment = new Environment();
        environment.setKey(dto.key());
        environment.setDescription(dto.description());
        environment.setNamespace(namespaceEntity);
        environment.setCreatedAt(LocalDateTime.now());

        Environment saved = environmentRepo.save(environment);
        log.info("Environment created with key='{}'", saved.getKey());

        return toResponseDTO(saved);
    }

    public Optional<EnvironmentResponseDTO> update(String namespaceKey, String envKey, EnvironmentCreateDTO dto) {
        log.info("Updating environment with key='{}' for namespace='{}'", envKey, namespaceKey);

        return environmentRepo.findByKeyAndNamespace_Key(envKey, namespaceKey)
                .map(existing -> {
                    existing.setKey(dto.key());
                    existing.setDescription(dto.description());
                    existing.setUpdatedAt(LocalDateTime.now());
                    Environment updated = environmentRepo.save(existing);
                    log.info("Environment updated with key='{}'", updated.getKey());
                    return toResponseDTO(updated);
                });
    }

    //Add metodo delete

    private EnvironmentResponseDTO toResponseDTO(Environment environment) {
        return new EnvironmentResponseDTO(
                environment.getKey(),
                environment.getDescription(),
                environment.getNamespace().getKey(),
                environment.getCreatedAt(),
                environment.getUpdatedAt()
        );
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\KafkaNotificationService.java`

```java
package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.responseDTO.ConfigEntryResponseDTO;
import io.github.configservice.config_service.event.EventType;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.mapper.ConfigEntryMapper;
import io.github.configservice.config_service.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaNotificationService {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationService.class);

    private final KafkaProducer kafkaProducerService;

    public KafkaNotificationService(KafkaProducer kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public void notifyConfigCreation(List<ConfigEntry> savedConfigs, String namespace, String environment) {
        if (CollectionUtils.isEmpty(savedConfigs)) {
            log.warn("No configurations to notify Kafka for namespace='{}', environment='{}'", namespace, environment);
            return;
        }

        List<String> configKeys = savedConfigs.stream()
                .map(ConfigEntry::getKey)
                .collect(Collectors.toList());

        kafkaProducerService.publishEvent(EventType.CREATE, namespace, environment, configKeys);

        if (isSingleConfig(savedConfigs)) {
            log.debug("Kafka CREATE event published for config: key='{}'", configKeys.get(0));
        } else {
            log.info("Kafka CREATE event published for {} new configurations in namespace='{}', environment='{}'",
                    configKeys.size(), namespace, environment);
        }
    }

    public void notifyConfigUpdate(String namespace, String environment, String configKey) {
        kafkaProducerService.publishEvent(EventType.UPDATE, namespace, environment, configKey);
        log.debug("Kafka UPDATE event published for config: key='{}'", configKey);
    }


    public void notifyConfigDeletion(String namespace, String environment, String configKey) {
        kafkaProducerService.publishEvent(EventType.DELETE, namespace, environment, configKey);
        log.debug("Kafka DELETE event published for config: key='{}'", configKey);
    }

    private boolean isSingleConfig(List<ConfigEntry> config) {
        return config.size() == 1;
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\NamespaceService.java`

```java
package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.createDTO.NamespaceCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.NamespaceResponseDTO;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NamespaceService {

    private static final Logger log = LoggerFactory.getLogger(NamespaceService.class);

    private final NamespaceRepository namespaceRepo;

    public NamespaceService(NamespaceRepository namespaceRepo) {
        this.namespaceRepo = namespaceRepo;
    }

    public List<NamespaceResponseDTO> findAll() {
        return namespaceRepo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<NamespaceResponseDTO> findByKey(String key) {
        return namespaceRepo.findByKey(key)
                .map(this::toResponseDTO);
    }

    public NamespaceResponseDTO createNamespace(NamespaceCreateDTO dto) {
        log.info("Creating namespace with key='{}'", dto.key());

        //Criar exceção NamespaceAlreadyExistsException
        if (namespaceRepo.existsByKey(dto.key()).isPresent()){
            log.info("Namespace with key='{}' already exists. Reusing it.", dto.key());
            throw new RuntimeException("Namespace with key=" + dto.key() + " already exists. Reusing it.");
        }

        Namespace namespace = new Namespace();
        namespace.setKey(dto.key());
        namespace.setDescription(dto.description());

        Namespace saved = namespaceRepo.save(namespace);
        log.info("Namespace created with key='{}'", saved.getKey());

        return toResponseDTO(saved);
    }

    //add metodo update
    //add metodo delete

    public NamespaceResponseDTO toResponseDTO(Namespace namespace) {
        return new NamespaceResponseDTO(
                namespace.getKey(),
                namespace.getDescription(),
                namespace.getCreatedAt(),
                namespace.getUpdatedAt()
        );
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\SyncService.java`

```java
package io.github.configservice.config_service.service;

import io.github.configservice.config_service.producer.KafkaProducer;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final ConfigEntryRepository configRepo;
    private final KafkaProducer kafkaProducerService;


    public SyncService(ConfigEntryRepository configRepo, KafkaProducer kafkaProducerService) {
        this.configRepo = configRepo;
        this.kafkaProducerService = kafkaProducerService;
    }

    public boolean forceSync(String namespace, String env) {
        boolean environmentExists = configRepo.existsByEnvironment_KeyAndEnvironment_Namespace_Key(env, namespace);

        if (!environmentExists) {
            log.warn("Sync ignored - environment not found: namespace='{}', env='{}'", namespace, env);
            return false;
        }

        log.info("Triggering sync-force for namespace='{}', env='{}'", namespace, env);
        //kafkaProducerService.publishEvent(EventType.SYNC ,namespace, env, null);
        return true;
    }
}
```

#### `src\main\java\io\github\configservice\config_service\service\validator\ConfigEntryValidator.java`

```java
package io.github.configservice.config_service.service.validator;

import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.config_service.exception.ConfigAlreadyExistsException;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConfigEntryValidator {

    private final ConfigEntryRepository configRepo;
    private final Validator validator;

    public ConfigEntryValidator(ConfigEntryRepository configRepo, Validator validator) {
        this.configRepo = configRepo;
        this.validator = validator;
    }

    public void validateCreateRequest(List<ConfigEntryCreateDTO> configDTOs) {
        if (CollectionUtils.isEmpty(configDTOs)) {
            throw new IllegalArgumentException("Configuration list cannot be null or empty");
        }

        configDTOs.forEach(this::validateConfigEntry);

        validateNoDuplicatesInList(configDTOs);
    }

    public void validateUpdateRequest(ConfigEntryCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        validateConfigEntry(dto);
    }

    public void validateNoDuplicateKeys(List<ConfigEntryCreateDTO> configDTOs, String namespaceKey, String environmentKey) {
        List<String> existingKeys = configDTOs.stream()
                .filter(config -> configExists(config.key(), namespaceKey, environmentKey))
                .map(ConfigEntryCreateDTO::key)
                .collect(Collectors.toList());

        if (!existingKeys.isEmpty()) {
            String duplicateKeys = String.join(", ", existingKeys);
            throw new ConfigAlreadyExistsException("Configurations already exist for keys: " + duplicateKeys);
        }
    }

    private boolean configExists(String key, String namespaceKey, String environmentKey) {
        return configRepo.existsByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, environmentKey, namespaceKey)
                .isPresent();
    }

    private void validateConfigEntry(ConfigEntryCreateDTO dto) {
        Set<ConstraintViolation<ConfigEntryCreateDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }
    }

    private void validateNoDuplicatesInList(List<ConfigEntryCreateDTO> configDTOs) {
        List<String> keys = configDTOs.stream()
                .map(ConfigEntryCreateDTO::key)
                .collect(Collectors.toList());

        List<String> duplicateKeys = keys.stream()
                .collect(Collectors.groupingBy(key -> key, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        if (!duplicateKeys.isEmpty()) {
            String duplicates = String.join(", ", duplicateKeys);
            throw new IllegalArgumentException("Duplicate keys found in request: " + duplicates);
        }
    }
}
```

#### `src\main\resources\logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{correlationId:-NO_CORRELATION_ID}] %logger{36} - %msg%n"/>
    <property name="CONSOLE_LOG_PATTERN" value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr([%thread]){blue} %clr(%-5level){highlight} %clr([%X{correlationId:-NO_CORRELATION_ID}]){magenta} %clr(%logger{36}){cyan} %clr(-){faint} %msg%n"/>
    <property name="LOG_FILE" value="logs/config-service.log"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/config-service.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <maxFileSize>100MB</maxFileSize>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/config-service-json.log</file>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <includeMdc>true</includeMdc>
            <customFields>{"service":"config-service"}</customFields>
            <fieldNames>
                <timestamp>@timestamp</timestamp>
                <message>message</message>
                <level>level</level>
                <thread>thread</thread>
                <logger>logger</logger>
            </fieldNames>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/config-service-json.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxHistory>7</maxHistory>
            <maxFileSize>50MB</maxFileSize>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="FILE"/>
        <queueSize>1024</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <includeCallerData>false</includeCallerData>
    </appender>

    <springProfile name="dev,local">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="ASYNC_FILE"/>
        </root>

        <logger name="org.springframework" level="INFO"/>
        <logger name="org.apache.kafka" level="WARN"/>
        <logger name="org.hibernate" level="WARN"/>
    </springProfile>

    <springProfile name="test">
        <root level="WARN">
            <appender-ref ref="CONSOLE"/>
        </root>

        <logger name="io.github.configservice" level="INFO"/>
    </springProfile>

    <springProfile name="prod,production">
        <root level="INFO">
            <appender-ref ref="ASYNC_FILE"/>
            <appender-ref ref="JSON_FILE"/>
        </root>

        <appender name="ERROR_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
                <level>ERROR</level>
            </filter>
            <encoder>
                <pattern>${CONSOLE_LOG_PATTERN}</pattern>
                <charset>UTF-8</charset>
            </encoder>
        </appender>

        <root>
            <appender-ref ref="ERROR_CONSOLE"/>
        </root>

        <logger name="io.github.configservice" level="DEBUG" additivity="false">
            <appender-ref ref="ASYNC_FILE"/>
            <appender-ref ref="JSON_FILE"/>
        </logger>

        <logger name="org.springframework" level="WARN"/>
        <logger name="org.apache.kafka" level="INFO"/>
        <logger name="org.hibernate.SQL" level="INFO"/>
        <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="WARN"/>
        <logger name="com.zaxxer.hikari" level="WARN"/>
    </springProfile>

    <logger name="io.github.configservice.config_service.producer.KafkaProducer" level="DEBUG"/>
    <logger name="io.github.configservice.config_service.config.CorrelationIdFilter" level="DEBUG"/>

</configuration>
```

#### `src\test\java\io\github\configservice\config_service\ConfigServiceApplicationTests.java`

```java
package io.github.configservice.config_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConfigServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
```

---

