package io.github.configservice.config_service.dto.responseDTO;

import java.time.LocalDateTime;

public record ConfigEntryResponseDTO(String key, String value, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
