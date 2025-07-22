package io.github.configservice.config_service.event;

public record ConfigChangedEvent(
        String type,
        String namespace,
        String environment,
        String key,
        String correlationId) {
}
