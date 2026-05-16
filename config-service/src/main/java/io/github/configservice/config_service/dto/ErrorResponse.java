package io.github.configservice.config_service.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String code,
                            String message,
                            LocalDateTime timestamp) {
}
