package io.github.configservice.config_service.dto.createDTO;

import jakarta.validation.constraints.NotBlank;

public record ConfigEntryCreateDTO(@NotBlank(message = "Key is mandatory") String key, @NotBlank(message = "Value is mandatory") String value, String description) {
}
