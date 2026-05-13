package io.github.clientlibrary.client_library.consumer;

import io.github.clientlibrary.client_library.event.ConfigEvent;
import io.github.clientlibrary.client_library.service.sync.ConfigSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        prefix = "config.client.kafka",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ConfigEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ConfigEventConsumer.class);
    private final ConfigSyncService configSyncService;

    public ConfigEventConsumer(ConfigSyncService configSyncService) {
        this.configSyncService = configSyncService;
    }

    @KafkaListener(
            topics = "#{'${config.client.kafka.topics:}'.empty ? {} : '${config.client.kafka.topics}'.split(',')}",
            groupId = "${config.client.kafka.group-id:${spring.application.name:default-group}}",
            containerFactory = "#{@configEventListenerContainerFactory ?: 'kafkaListenerContainerFactory'}"
    )
    public void consumerConfigEvent(
            @Payload ConfigEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
            Acknowledgment acknowledgment
    ) {
        log.debug("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            boolean success = configSyncService.processConfigEvent(event);

            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.debug("Message acknowledged: correlationId={}, success={}", event.correlationId(), success);
            }

        } catch (Exception e) {
            log.error("Error processing config event from topic: {}, correlationId: {}, event: {}",
                    topic, event.correlationId(), event.toString(), e);

            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.debug("Message acknowledged despite error to prevent reprocessing: correlationId={}", event.correlationId());
            }
        }
    }
}
