package io.github.configservice.config_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"key", "environment_id"})
})
public class ConfigEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "The key must be in kebab-case (e.g., my-config-key)")
    @NotBlank(message = "The key cannot be empty")
    @Size(min = 3, max = 100, message = "The key must be between 3 and 100 characters long")
    @Column(nullable = false, length = 100)
    private String key;

    @NotNull(message = "Value is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "environment_id", nullable = false)
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
