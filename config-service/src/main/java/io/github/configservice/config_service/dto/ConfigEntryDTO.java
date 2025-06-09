package io.github.configservice.config_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfigEntryDTO(@NotBlank String namespace, @NotBlank String environment, @NotBlank String keyName, @NotBlank String value) {
}
