package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record EnvironmentResponseDTO(String key, String description, String namespaceKey,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
}
