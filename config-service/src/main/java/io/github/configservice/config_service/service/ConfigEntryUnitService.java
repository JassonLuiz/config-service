package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfigEntryUnitService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryUnitService.class);

    private final ConfigEntryRepository configRepo;

    public ConfigEntryUnitService(ConfigEntryRepository configRepo) {
        this.configRepo = configRepo;
    }

    public ConfigEntry save(String key, String value, String description, Environment environment) {
        String envKey = environment.getKey();
        String namespaceKey = environment.getNamespace().getKey();

        log.debug("Searching for config entry with key='{}' in env='{}' and namespace='{}'", key, envKey, namespaceKey);

        ConfigEntry entry = configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(
                        key,
                        environment.getKey(),
                        environment.getNamespace().getKey()
                )
                .orElseGet(() -> {
                    log.info("No existing config entry found. Creating new entry for key='{}'", key);
                    return new ConfigEntry();
                });

        entry.setKey(key);
        entry.setValue(value);
        entry.setDescription(description);
        entry.setUpdatedAt(LocalDateTime.now());
        entry.setEnvironment(environment);

        return configRepo.save(entry);
    }
}
