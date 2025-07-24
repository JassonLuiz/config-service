package io.github.clientlibrary.client_library.dto;

import java.time.LocalDateTime;

public record ConfigEntryResponseDTO(
        String key,
        String value,
        LocalDateTime updatedAt
) {
}
