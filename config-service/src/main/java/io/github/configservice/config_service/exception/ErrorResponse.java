package io.github.configservice.config_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message,
                            String details,
                            int status,
                            String traceId,
                            LocalDateTime timestamp) {
}
