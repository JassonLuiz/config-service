package io.github.configservice.config_service.service.validator;

import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.config_service.exception.ConfigAlreadyExistsException;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConfigEntryValidator {

    private final ConfigEntryRepository configRepo;
    private final Validator validator;

    public ConfigEntryValidator(ConfigEntryRepository configRepo, Validator validator) {
        this.configRepo = configRepo;
        this.validator = validator;
    }

    public void validateCreateRequest(List<ConfigEntryCreateDTO> configDTOs) {
        if (CollectionUtils.isEmpty(configDTOs)) {
            throw new IllegalArgumentException("Configuration list cannot be null or empty");
        }

        configDTOs.forEach(this::validateConfigEntry);

        validateNoDuplicatesInList(configDTOs);
    }

    public void validateUpdateRequest(ConfigEntryCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        validateConfigEntry(dto);
    }

    public void validateNoDuplicateKeys(List<ConfigEntryCreateDTO> configDTOs, String namespaceKey, String environmentKey) {
        List<String> existingKeys = configDTOs.stream()
                .filter(config -> configExists(config.key(), namespaceKey, environmentKey))
                .map(ConfigEntryCreateDTO::key)
                .collect(Collectors.toList());

        if (!existingKeys.isEmpty()) {
            String duplicateKeys = String.join(", ", existingKeys);
            throw new ConfigAlreadyExistsException("Configurations already exist for keys: " + duplicateKeys);
        }
    }

    private boolean configExists(String key, String namespaceKey, String environmentKey) {
        return configRepo.existsByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(key, environmentKey, namespaceKey)
                .isPresent();
    }

    private void validateConfigEntry(ConfigEntryCreateDTO dto) {
        Set<ConstraintViolation<ConfigEntryCreateDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }
    }

    private void validateNoDuplicatesInList(List<ConfigEntryCreateDTO> configDTOs) {
        List<String> keys = configDTOs.stream()
                .map(ConfigEntryCreateDTO::key)
                .collect(Collectors.toList());

        List<String> duplicateKeys = keys.stream()
                .collect(Collectors.groupingBy(key -> key, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        if (!duplicateKeys.isEmpty()) {
            String duplicates = String.join(", ", duplicateKeys);
            throw new IllegalArgumentException("Duplicate keys found in request: " + duplicates);
        }
    }
}
