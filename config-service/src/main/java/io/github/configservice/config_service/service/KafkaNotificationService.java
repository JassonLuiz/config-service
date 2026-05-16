package io.github.configservice.config_service.service;

import io.github.configservice.config_service.event.EventType;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaNotificationService {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationService.class);

    private final KafkaProducer kafkaProducerService;

    public KafkaNotificationService(KafkaProducer kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public void notifyConfigCreation(List<ConfigEntry> savedConfigs, String namespace, String environment) {
        if (CollectionUtils.isEmpty(savedConfigs)) {
            log.warn("No configurations to notify Kafka for namespace='{}', environment='{}'", namespace, environment);
            return;
        }

        List<String> configKeys = savedConfigs.stream()
                .map(ConfigEntry::getKey)
                .collect(Collectors.toList());

        kafkaProducerService.publishEvent(EventType.CREATE, namespace, environment, configKeys);

        if (isSingleConfig(savedConfigs)) {
            log.debug("Kafka CREATE event published for config: key='{}'", configKeys.get(0));
        } else {
            log.info("Kafka CREATE event published for {} new configurations in namespace='{}', environment='{}'",
                    configKeys.size(), namespace, environment);
        }
    }

    public void notifyConfigUpdate(String namespace, String environment, String configKey) {
        kafkaProducerService.publishEvent(EventType.UPDATE, namespace, environment, configKey);
        log.debug("Kafka UPDATE event published for config: key='{}'", configKey);
    }


    public void notifyConfigDeletion(String namespace, String environment, String configKey) {
        kafkaProducerService.publishEvent(EventType.DELETE, namespace, environment, configKey);
        log.debug("Kafka DELETE event published for config: key='{}'", configKey);
    }

    private boolean isSingleConfig(List<ConfigEntry> config) {
        return config.size() == 1;
    }
}
