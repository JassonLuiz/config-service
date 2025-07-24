package io.github.clientlibrary.client_library.consumer;

import io.github.clientlibrary.client_library.event.ConfigChangedEvent;
import io.github.clientlibrary.client_library.service.ConfigSyncService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigEventConsumer {

    private final ConfigSyncService configSyncService;

    public ConfigEventConsumer(ConfigSyncService configSyncService) {
        this.configSyncService = configSyncService;
    }

    @KafkaListener(
            topics = "#{'${config.kafka.topics}'.split(',')}",
            groupId = "${spring.application.name}"
    )
    public void consume(ConfigChangedEvent event) {
        switch (event.type()) {
            case SYNC -> configSyncService.handleSync(event.namespace(), event.environment());
            case UPDATE -> configSyncService.handleUpdate(event.namespace(), event.environment(), event.key());
            case DELETE -> configSyncService.handleDelete(event.namespace(), event.environment(), event.key());
            default -> throw new IllegalArgumentException("Unknown event type: " + event.type());
        }
    }
}
