package io.github.configservice.config_service.producer;

import io.github.configservice.contracts.event.ConfigEvent;
import io.github.configservice.contracts.event.EventType;
import io.github.configservice.config_service.service.DeadLetterQueueService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, ConfigEvent> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private final DeadLetterQueueService dlqService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, ConfigEvent> kafkaTemplate, KafkaAdmin kafkaAdmin, DeadLetterQueueService dlqService) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdmin = kafkaAdmin;
        this.dlqService = dlqService;
    }

    public void publishEvent(EventType eventType, String namespace, String env, List<String> configKeys){
        if (configKeys == null || configKeys.isEmpty()) {
            logger.warn("Attempted to publish event with empty configs list");
            return;
        }

        String correlationId = getOrGenerateCorrelationId();
        ConfigEvent event = new ConfigEvent(eventType, namespace, env, configKeys, correlationId);
        String topicName = buildTopicName(namespace, env);

        ensureTopicExists(topicName);
        sendEvent(topicName, event);
    }

    public void publishEvent(EventType eventType, String namespace, String env, String configKey) {
        publishEvent(eventType, namespace, env, List.of(configKey));
    }

    private void sendEvent(String topic,ConfigEvent event) {

        kafkaTemplate.send(topic, event)
                .toCompletableFuture()
                .thenAccept(result -> {
                    if (event.isSingleConfig()) {
                        logger.info("Kafka event sent successfully. CorrelationId={}, Key={}, Offset={}, Topic={}",
                                event.correlationId(),
                                event.getSingleConfigKey(),
                                result.getRecordMetadata().offset(),
                                result.getRecordMetadata().topic());
                    } else {
                        logger.info("Kafka batch event sent successfully. CorrelationId={}, ConfigCount={}, Offset={}, Topic={}",
                                event.correlationId(),
                                event.getConfigCount(),
                                result.getRecordMetadata().offset(),
                                result.getRecordMetadata().topic());
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Failed to send Kafka event, saving to DLQ. EventType={}, ConfigCount={}, Keys={}, CorrelationId={}, Error={}",
                            event.type(),
                            event.getConfigCount(),
                            event.configKeys(),
                            event.correlationId(),
                            ex.getMessage());

                    try {
                        dlqService.saveFailedEvent(topic, event, ex.getMessage());

                        logger.info("Event saved to DLQ for later retry. CorrelationId={}, Topic={}",
                                event.correlationId(), topic);
                    } catch (Exception dlqError) {
                        logger.error("CRITICAL: Failed to save event to DLQ! Event might be lost! " +
                                        "CorrelationId={}, Event={}, KafkaError={}, DLQError={}",
                                event.correlationId(), event, ex.getMessage(), dlqError.getMessage(), dlqError);
                    }
                    return null;
                });
    }

    private void ensureTopicExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())){
            Set<String> topics = adminClient.listTopics().names().get();
            if(!topics.contains(topicName)){
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                logger.info("Topic '{}' dynamically created.", topicName);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Error checking/creating topic '{}': {}", topicName, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private String buildTopicName(String namespace, String env) {
        return "config.%s.%s".formatted(
                namespace.replaceAll("[^a-zA-Z0-9_-]", "-"),
                env.replaceAll("[^a-zA-Z0-9_-]", "-")
        );
    }

    private String getOrGenerateCorrelationId(){
        return MDC.get("correlationId") != null ? MDC.get("correlationId") : UUID.randomUUID().toString();
    }
}
