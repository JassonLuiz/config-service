package io.github.configservice.config_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String code,
                            String message,
                            LocalDateTime timestamp) {
}
