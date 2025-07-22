package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Environment {

    @Id
    @GeneratedValue
    private UUID id;

    private String key;
    private String description;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfigEntry> configs = new ArrayList<>();

    public Environment() {}

    public Environment(UUID id, String key, String descricao, Namespace namespace) {
        this.id = id;
        this.key = key;
        this.description = descricao;
        this.namespace = namespace;
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
}
