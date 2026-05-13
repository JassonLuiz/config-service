package io.github.configservice.config_service.event;

import java.time.LocalDateTime;
import java.util.List;

public record ConfigEvent(
        EventType type,
        String namespace,
        String environment,
        List<String> configKeys,
        String correlationId,
        LocalDateTime timestamp
) {

    public ConfigEvent(EventType type, String namespace, String environment, List<String> configKeys, String correlationId) {
        this(type, namespace, environment, configKeys, correlationId, LocalDateTime.now());
    }

    public int getConfigCount() {
        return configKeys != null ? configKeys.size() : 0;
    }

    public boolean hasConfigKeys() {
        return configKeys != null && !configKeys.isEmpty();
    }

    public boolean isSingleConfig() {
        return getConfigCount() == 1;
    }

    public String getSingleConfigKey() {
        if (isSingleConfig()) {
            return configKeys.getFirst();
        }
        throw new IllegalStateException("Event contains multiple config keys, expected single key");
    }
}
