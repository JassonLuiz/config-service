package io.github.configservice.config_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "dead_letter_queue",
        indexes = {
                @Index(name = "idx_dlq_status", columnList = "status"),
                @Index(name = "idx_dlq_retry_count", columnList = "retry_count"),
                @Index(name = "idx_dlq_failed_at", columnList = "failed_at")
        }
)
public class DeadLetterMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "event_payload", columnDefinition = "TEXT", nullable = false)
    private String eventPayload;

    @Column(name = "target_topic", nullable = false, length = 255)
    private String targetTopic;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;

    @Column(name = "last_retry_at", nullable = false)
    private LocalDateTime lastRetryAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "event_type", length = 20)
    private String eventType;

    @Column(name = "namespace", length = 100)
    private String namespace;

    @Column(name = "environment", length = 50)
    private String environment;

    @Column(name = "config_count")
    private Integer configCount;

    public DeadLetterMessage() {
        this.retryCount = 0;
        this.status = "PENDING";
    }

    public DeadLetterMessage(String eventPayload, String targetTopic, String errorMessage,
                             LocalDateTime failedAt, String correlationId, String eventType,
                             String namespace, String environment, Integer configCount) {
        this.eventPayload = eventPayload;
        this.targetTopic = targetTopic;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
        this.correlationId = correlationId;
        this.eventType = eventType;
        this.namespace = namespace;
        this.environment = environment;
        this.configCount = configCount;
        this.retryCount = 0;
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public String getTargetTopic() {
        return targetTopic;
    }

    public void setTargetTopic(String targetTopic) {
        this.targetTopic = targetTopic;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public LocalDateTime getLastRetryAt() {
        return lastRetryAt;
    }

    public void setLastRetryAt(LocalDateTime lastRetryAt) {
        this.lastRetryAt = lastRetryAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public Integer getConfigCount() {
        return configCount;
    }

    public void setConfigCount(Integer configCount) {
        this.configCount = configCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
    }

    public boolean hasExceededMaxRetries() {
        return this.retryCount >= 3;
    }

    public void markAsFailed() {
        this.status = "FAILED";
    }

    public void markAsRetrying() {
        this.status = "RETRYING";
    }

    public void markAsResolved() {
        this.status = "RESOLVED";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeadLetterMessage that = (DeadLetterMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeadLetterMessage{" +
                "id=" + id +
                ", targetTopic='" + targetTopic + '\'' +
                ", status='" + status + '\'' +
                ", retryCount=" + retryCount +
                ", correlationId='" + correlationId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", namespace='" + namespace + '\'' +
                ", environment='" + environment + '\'' +
                ", configCount=" + configCount +
                ", failedAt=" + failedAt +
                '}';
    }
}
