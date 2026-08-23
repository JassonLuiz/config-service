package io.github.configservice.contracts.dto;

import java.time.LocalDateTime;

public record ConfigEntryResponseDTO(
        String key,
        String value,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
