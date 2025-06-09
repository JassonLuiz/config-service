package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "config_entry_history")
public class ConfigEntryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long configEntryId;
    private String keyName;
    private String oldValue;
    private String newValue;
    private LocalDateTime modifiedAt;

    public ConfigEntryHistory() {}

    public ConfigEntryHistory(Long configEntryId, String keyName, String oldValue, String newValue, LocalDateTime modifiedAt) {
        this.configEntryId = configEntryId;
        this.keyName = keyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.modifiedAt = modifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigEntryId() {
        return configEntryId;
    }

    public void setConfigEntryId(Long configEntryId) {
        this.configEntryId = configEntryId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
