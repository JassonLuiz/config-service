package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record ConfigHistoryResponseDTO(
        Long id,
        String namespace,
        String environment,
        String configKey,
        String oldValue,
        String newValue,
        String operation,
        LocalDateTime changedAt,
        String changedBy,
        String correlationId,
        String changeDescription
) {

    public ConfigHistoryResponseDTO(
            Long id,
            String namespace,
            String environment,
            String configKey,
            String oldValue,
            String newValue,
            String operation,
            LocalDateTime changedAt,
            String changedBy,
            String correlationId) {
        this(id, namespace, environment, configKey, oldValue, newValue,
                operation, changedAt, changedBy, correlationId, null);
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
        return String.format("%s: '%s' → '%s'", operation, oldValue, newValue);
    }
}
