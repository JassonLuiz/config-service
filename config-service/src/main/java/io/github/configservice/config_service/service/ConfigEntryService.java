package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.ConfigEntryBatchDTO;
import io.github.configservice.config_service.dto.ConfigEntryResponseDTO;
import io.github.configservice.config_service.exception.ConfigNotFoundException;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigEntryService {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryService.class);

    private final ConfigEntryRepository configRepo;
    private final EnvironmentRepository envRepo;
    private final KafkaProducerService kafkaProducerService;
    private final NamespaceService namespaceService;
    private final EnvironmentService environmentService;
    private final ConfigEntryUnitService configEntryUnitService;

    public ConfigEntryService(ConfigEntryRepository configRepo,
                              EnvironmentRepository envRepo,
                              KafkaProducerService kafkaProducerService,
                              NamespaceService namespaceService,
                              EnvironmentService environmentService,
                              ConfigEntryUnitService configEntryUnitService) {
        this.configRepo = configRepo;
        this.envRepo = envRepo;
        this.kafkaProducerService = kafkaProducerService;
        this.namespaceService = namespaceService;
        this.environmentService = environmentService;
        this.configEntryUnitService = configEntryUnitService;
    }

    public List<ConfigEntryResponseDTO> getAllByNamespaceAndEnvironment(String namespaceKey, String environmentKey) {
        log.info("Fetching all configurations for namespace='{}', environment='{}'", namespaceKey, environmentKey);

        return configRepo
                .findAllByEnvironment_KeyAndEnvironment_Namespace_Key(environmentKey, namespaceKey)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ConfigEntryResponseDTO> getByKey(String namespace, String env, String key) {
        log.info("Searching for configuration by key='{}' in the namespace='{}', environment='{}'", key, namespace, env);

        return configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, env, namespace)
                .map(this::toDTO);
    }

    @Transactional
    public void saveConfigurationTree(ConfigEntryBatchDTO entry) {
        log.info("Starting configuration tree save for namespace='{}'", entry.getKey());

        Namespace namespace = namespaceService.findOrCreate(entry.getKey(), entry.getDescription());

        entry.getEnvironments().forEach(envDto -> {
            Environment environment = environmentService.findOrCreate(envDto.getKey(), envDto.getDescription(), namespace);

            int configCount = envDto.getConfigs().size();

            envDto.getConfigs().forEach(configDto -> {
                configEntryUnitService.save(configDto.getKey(), configDto.getValue(), configDto.getDescription(), environment);
            });

            if (configCount == 1) {
                String configKey = envDto.getConfigs().get(0).getKey();
                kafkaProducerService.publishUpdateEvent(namespace.getKey(), environment.getKey(), configKey);
            } else {
                kafkaProducerService.publishSyncForceEvent(namespace.getKey(), environment.getKey());
            }
        });
    }

    public boolean deleteConfig(String namespace, String env, String key) {
        log.info("Trying to remove configuration key='{}' int the namespace='{}', environment='{}'", key, namespace, env);

        Optional<ConfigEntry> existing = configRepo
                .findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, env, namespace);

        if (existing.isPresent()){
            configRepo.delete(existing.get());
            log.info("Configuration removed successfully. key='{}'", key);
            kafkaProducerService.publishDeleteEvent(namespace, env, key);
            return true;
        } else {
            log.warn("Configuration for deletion not found. key='{}'", key);
            return false;
        }
    }

    private ConfigEntryResponseDTO toDTO(ConfigEntry configEntry) {
        return new ConfigEntryResponseDTO(
                configEntry.getKey(),
                configEntry.getValue(),
                configEntry.getUpdatedAt()
        );
    }
}
