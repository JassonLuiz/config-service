package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "config_history",
        indexes = {
                @Index(name = "idx_history_namespace", columnList = "namespace"),
                @Index(name = "idx_history_env", columnList = "environment"),
                @Index(name = "idx_history_key", columnList = "config_key"),
                @Index(name = "idx_history_timestamp", columnList = "changed_at"),
                @Index(name = "idx_history_composite", columnList = "namespace, environment, config_key, changed_at")
        }
)
public class ConfigHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "namespace", nullable = false, length = 100)
    private String namespace;

    @Column(name = "environment", nullable = false, length = 50)
    private String environment;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Lob
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "operation", nullable = false, length = 20)
    private String operation;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "change_description", columnDefinition = "TEXT")
    private String changeDescription;

    public ConfigHistory() {
    }

    public ConfigHistory(String namespace, String environment, String configKey,
                         String oldValue, String newValue, String operation,
                         LocalDateTime changedAt, String changedBy, String correlationId) {
        this.namespace = namespace;
        this.environment = environment;
        this.configKey = configKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operation = operation;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
        this.correlationId = correlationId;
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

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isCreation() {
        return "CREATE".equals(operation);
    }

    public boolean isUpdate() {
        return "UPDATE".equals(operation);
    }

    public boolean isDeletion() {
        return "DELETE".equals(operation);
    }

    public String getChangeSummary() {
        return String.format("%s: '%s' -> '%s'", operation, oldValue, newValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigHistory that = (ConfigHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ConfigHistory{" +
                "id=" + id +
                ", namespace='" + namespace + '\'' +
                ", environment='" + environment + '\'' +
                ", configKey='" + configKey + '\'' +
                ", operation='" + operation + '\'' +
                ", changedAt=" + changedAt +
                ", changedBy='" + changedBy + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
