package io.github.configservice.config_service.service;

import io.github.configservice.config_service.exception.ConfigNotFoundException;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.ConfigEntryHistory;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ConfigEntryService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryService.class);

    private final ConfigEntryRepository repository;
    private final ConfigEntryUpdater updater;
    private final ConfigEntryHistoryService historyService;
    private final ConfigChangePublisher publisher;

    public ConfigEntryService(ConfigEntryRepository repository, ConfigEntryUpdater updater, ConfigEntryHistoryService historyService, ConfigChangePublisher publisher) {
        this.repository = repository;
        this.updater = updater;
        this.historyService = historyService;
        this.publisher = publisher;
    }

    @Transactional
    public ConfigEntry create(ConfigEntry config){
        var now = LocalDateTime.now();
        config.setUpdatedAt(now);

        log.info("Start to create configuration: {}", config);

        return repository.findByNamespaceAndEnvironmentAndKeyName(
                config.getNamespace(), config.getEnvironment(), config.getKeyName()
                )
                .map(existing -> updateConfig(existing, config.getValue(), now))
                .orElseGet(() -> createNewConfig(config));
    }

    public ConfigEntry updateConfig(ConfigEntry existing, String newValue, LocalDateTime now){
        log.info("Updating existing config. ID: {}, chave: {}", existing.getId(), existing.getKeyName());

        historyService.save(
                new ConfigEntryHistory(
                        existing.getId(),
                        existing.getKeyName(),
                        existing.getValue(),
                        newValue,
                        now
                )
        );

        ConfigEntry updated = repository.save(updater.applyUpdate(existing, newValue, now));

        log.info("Updated configuration: {}", updated);
        publisher.publish(updated);
        return updated;
    }

    public ConfigEntry createNewConfig(ConfigEntry config){
        config.setVersion(1);
        ConfigEntry saved = repository.save(config);

        log.info("Created configuration: {}", saved);
        publisher.publish(saved);
        return saved;
    }

    public ConfigEntry findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ConfigNotFoundException("Configuration with ID " + id + " not found."));
    }
}
