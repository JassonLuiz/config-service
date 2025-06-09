package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "config_entries")
public class ConfigEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namespace;
    private String environment;
    private String keyName;
    private String value;
    private Integer version;
    private LocalDateTime updatedAt;

    public ConfigEntry() {}

    public ConfigEntry(String namespace, String environment, String keyName, String value, Integer version, LocalDateTime updatedAt) {
        this.namespace = namespace;
        this.environment = environment;
        this.keyName = keyName;
        this.value = value;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
