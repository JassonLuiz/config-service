package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.ConfigEntryHistory;
import io.github.configservice.config_service.repository.ConfigEntryHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigEntryHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryHistoryService.class);
    private final ConfigEntryHistoryRepository repository;

    public ConfigEntryHistoryService(ConfigEntryHistoryRepository repository) {
        this.repository = repository;
    }

    public List<ConfigEntryHistory> getHistoryByConfig(Long configEntryId){
        log.info("Getting history for config entry ID: {}", configEntryId);
        return repository.findByConfigEntryId(configEntryId);
    }

    public ConfigEntryHistory save(ConfigEntryHistory history){
        ConfigEntryHistory saved = repository.save(history);
        log.info("Saved change history: {}", history);
        return saved;
    }
}
