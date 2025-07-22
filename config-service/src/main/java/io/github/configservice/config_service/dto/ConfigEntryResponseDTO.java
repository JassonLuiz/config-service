package io.github.configservice.config_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConfigEntryResponseDTO(String Key, String value, LocalDateTime updatedAt) {
}
