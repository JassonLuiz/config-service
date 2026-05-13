package io.github.configservice.config_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
}
