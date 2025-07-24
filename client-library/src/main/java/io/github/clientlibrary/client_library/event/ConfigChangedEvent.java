package io.github.clientlibrary.client_library.event;

public record ConfigChangedEvent(
        EventType type,
        String namespace,
        String environment,
        String key,
        String correlationId,
        String eventId
) {
}
