package io.github.configservice.config_service.dto.createDTO;

import jakarta.validation.constraints.NotBlank;

public record EnvironmentCreateDTO(@NotBlank@NotBlank(message = "Key is mandatory") String key, String description) {
}
