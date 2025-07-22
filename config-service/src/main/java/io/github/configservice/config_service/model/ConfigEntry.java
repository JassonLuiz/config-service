package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ConfigEntry {

    @Id
    @GeneratedValue
    private UUID id;

    private String key;
    private String value;
    private String description;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    public ConfigEntry() {}

    public ConfigEntry(UUID id, String key, String value, String description, LocalDateTime updatedAt, Environment environment) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
        this.updatedAt = updatedAt;
        this.environment = environment;
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
}
