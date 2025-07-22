package io.github.configservice.config_service.event;

import java.util.UUID;

public record ConfigChangedEvent(
        EventType type,
        String namespace,
        String environment,
        String key,
        String correlationId,
        String eventId) {
    public ConfigChangedEvent(EventType type, String namespace, String environment, String key, String correlationId) {
        this(type, namespace, environment, key, correlationId, UUID.randomUUID().toString());
    }
}
