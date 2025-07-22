package io.github.configservice.config_service.service;

import io.github.configservice.config_service.event.ConfigChangedEvent;
import io.github.configservice.config_service.event.EventType;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public KafkaProducerService(KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate, KafkaAdmin kafkaAdmin) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdmin = kafkaAdmin;
    }

    public void publishEvent(EventType eventType, String namespace, String env, String key){
        String correlationId = getOrGenerateCorrelationId();
        ConfigChangedEvent event = new ConfigChangedEvent(eventType, namespace, env, key, correlationId);
        sendEvent(buildTopicName(namespace, env), event);
    }

    private void sendEvent(String topic,ConfigChangedEvent event) {
        ensureTopicExists(topic);

        kafkaTemplate.send(topic, event)
                .toCompletableFuture()
                .thenAccept(result -> logger.info("Kafka event sent successfully. CorrelationId={}, Offset={}, Topic={}",
                        event.correlationId(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().topic()))
                .exceptionally(ex -> {
                    logger.error("Error sending Kafka event. CorrelationId={}", event.correlationId(), ex);
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
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);
        }
        return correlationId;
    }
}
