package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConfigEntryUpdater {

    public ConfigEntry applyUpdate(ConfigEntry existing, String newValue, LocalDateTime now){
        existing.setValue(newValue);
        existing.setUpdatedAt(now);
        existing.setVersion(existing.getVersion() + 1);
        return existing;
    }
}
