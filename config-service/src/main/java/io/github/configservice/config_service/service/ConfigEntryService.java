package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.ConfigEntryResponseDTO;
import io.github.configservice.config_service.event.EventType;
import io.github.configservice.config_service.exception.EnvironmentNotFoundException;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.mapper.ConfigEntryMapper;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import io.github.configservice.config_service.service.validator.ConfigEntryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigEntryService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryService.class);

    private final ConfigEntryRepository configRepo;
    private final EnvironmentRepository environmentRepository;
    private final KafkaNotificationService kafkaNotificationService;
    private final ConfigEntryValidator validator;
    private final ConfigEntryMapper mapper;

    public ConfigEntryService(ConfigEntryRepository configRepo, EnvironmentRepository environmentRepository,
                              KafkaNotificationService kafkaNotificationService, ConfigEntryValidator validator, ConfigEntryMapper mapper) {
        this.configRepo = configRepo;
        this.environmentRepository = environmentRepository;
        this.kafkaNotificationService = kafkaNotificationService;
        this.validator = validator;
        this.mapper = mapper;
    }

    public List<ConfigEntryResponseDTO> getAllByNamespaceAndEnvironment(String namespaceKey, String environmentKey) {
        log.info("Fetching all configurations for namespace='{}', environment='{}'", namespaceKey, environmentKey);

        List<ConfigEntry> configs = configRepo.findAllByEnvironment_KeyAndEnvironment_Namespace_Key(environmentKey, namespaceKey);
        return mapper.toResponseDTOList(configs);
    }

    public Optional<ConfigEntryResponseDTO> getByKey(String namespace, String env, String key) {
        log.info("Searching for configuration by key='{}' in the namespace='{}', environment='{}'", key, namespace, env);

        return configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, env, namespace)
                .map(mapper::toResponseDTO);
    }

    @Transactional
    public List<ConfigEntryResponseDTO> createConfiguration(String namespaceKey, String environmentKey, List<ConfigEntryCreateDTO> configDTOs) {
        log.info("Creating {} configurations for namespace='{}', environment='{}'",
                configDTOs.size(), namespaceKey, environmentKey);

        validator.validateCreateRequest(configDTOs);
        Environment environment = findEnvironmentOrThrow(namespaceKey, environmentKey);
        validator.validateNoDuplicateKeys(configDTOs, namespaceKey, environmentKey);

        List<ConfigEntry> savedConfigs = createConfigs(configDTOs, environment);

        kafkaNotificationService.notifyConfigCreation(savedConfigs, namespaceKey, environmentKey);

        log.info("Successfully created {} configurations for namespace='{}', environment='{}'",
                savedConfigs.size(), namespaceKey, environmentKey);

        return mapper.toResponseDTOList(savedConfigs);
    }

    @Transactional
    public Optional<ConfigEntryResponseDTO> updateConfiguration(String namespaceKey, String envKey, String configKey, ConfigEntryCreateDTO dto) {
        log.info("Updating config with key='{}' for namespace='{}', environment='{}'",
                configKey, namespaceKey, envKey);

        validator.validateUpdateRequest(dto);

        return configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(configKey, envKey, namespaceKey)
                .map(existing -> {
                    updateConfigEntry(existing, dto);
                    ConfigEntry updated = configRepo.save(existing);

                    kafkaNotificationService.notifyConfigUpdate(namespaceKey, envKey, configKey);

                    log.info("Configuration updated successfully. key='{}'", configKey);
                    return mapper.toResponseDTO(updated);
                });
    }

    @Transactional
    public boolean deleteConfiguration(String namespaceKey, String envkey, String configKey) {
        log.info("Trying to remove configuration key='{}' in the namespace='{}', environment='{}'", configKey, namespaceKey, envkey);

        Optional<ConfigEntry> existing = configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(configKey, envkey, namespaceKey);

        if (existing.isPresent()){
            configRepo.delete(existing.get());
            kafkaNotificationService.notifyConfigDeletion(namespaceKey, envkey, configKey);

            log.info("Configuration removed successfully. key='{}'", configKey);
            return true;
        } else {
            log.warn("Configuration for deletion not found. key='{}'", configKey);
            return false;
        }
    }

    private Environment findEnvironmentOrThrow(String namespaceKey, String environmentKey) {
        return environmentRepository
                .findByKeyAndNamespace_Key(environmentKey, namespaceKey)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        String.format("Environment not found: key='%s', namespace='%s'", environmentKey, namespaceKey)
                ));
    }

    private List<ConfigEntry> createConfigs(List<ConfigEntryCreateDTO> configDTOs, Environment environment) {
        LocalDateTime now = LocalDateTime.now();

        return configDTOs.stream()
                .map(dto -> createSingleConfigEntry(dto, environment, now))
                .map(configRepo::save)
                .peek(config -> log.debug("Configuration created successfully: key='{}'", config.getKey()))
                .collect(Collectors.toList());
    }

    private ConfigEntry createSingleConfigEntry(ConfigEntryCreateDTO dto, Environment environment, LocalDateTime createdAt) {
        ConfigEntry configEntry = new ConfigEntry();
        configEntry.setKey(dto.key());
        configEntry.setValue(dto.value());
        configEntry.setDescription(dto.description());
        configEntry.setEnvironment(environment);
        configEntry.setCreatedAt(createdAt);
        return configEntry;
    }

    private void updateConfigEntry(ConfigEntry existing, ConfigEntryCreateDTO dto) {
        existing.setValue(dto.value());
        existing.setDescription(dto.description());
    }
}
