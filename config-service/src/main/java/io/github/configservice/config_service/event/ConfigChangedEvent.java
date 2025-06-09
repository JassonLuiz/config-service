package io.github.configservice.config_service.event;

public record ConfigChangedEvent(
        String namespace,
        String environment,
        String keyName,
        String newValue,
        Integer version) {
}
