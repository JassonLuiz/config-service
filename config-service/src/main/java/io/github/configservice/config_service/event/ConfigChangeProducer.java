package io.github.configservice.config_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConfigChangeProducer {

    private final KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(ConfigChangeProducer.class);

    private static final String TOPIC = "config-updates";

    public ConfigChangeProducer(KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(ConfigChangedEvent event){
        log.info("Sending Kafka event: {}", event);
        kafkaTemplate.send(TOPIC, event.namespace(), event)
                .toCompletableFuture()
                .thenAccept(result -> log.info("Kafka event sent: {}", result))
                .exceptionally(error -> {
                    log.info("Error sending Kafka event: {}", error);
                    return null;
                });
    }
}
