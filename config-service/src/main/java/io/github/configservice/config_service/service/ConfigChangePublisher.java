package io.github.configservice.config_service.service;

import io.github.configservice.config_service.event.ConfigChangeProducer;
import io.github.configservice.config_service.event.ConfigChangedEvent;
import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.stereotype.Component;

@Component
public class ConfigChangePublisher {

    private final ConfigChangeProducer producer;

    public ConfigChangePublisher(ConfigChangeProducer producer) {
        this.producer = producer;
    }

    public void publish(ConfigEntry entry){
        ConfigChangedEvent event = new ConfigChangedEvent(
                entry.getNamespace(),
                entry.getEnvironment(),
                entry.getKeyName(),
                entry.getValue(),
                entry.getVersion()
        );
        producer.sendEvent(event);
    }
}
