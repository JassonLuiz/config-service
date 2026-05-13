package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record NamespaceResponseDTO(String key, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
