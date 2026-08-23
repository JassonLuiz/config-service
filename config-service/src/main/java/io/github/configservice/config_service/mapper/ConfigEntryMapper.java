package io.github.configservice.config_service.mapper;

import io.github.configservice.contracts.dto.ConfigEntryResponseDTO;
import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigEntryMapper {

    public ConfigEntryResponseDTO toResponseDTO(ConfigEntry configEntry) {
        if (configEntry == null) {
            return null;
        }

        return new ConfigEntryResponseDTO(
                configEntry.getKey(),
                configEntry.getValue(),
                configEntry.getDescription(),
                configEntry.getCreatedAt(),
                configEntry.getUpdatedAt()
        );
    }

    public List<ConfigEntryResponseDTO> toResponseDTOList(List<ConfigEntry> configEntries) {
        if (configEntries == null) {
            return List.of();
        }

        return configEntries.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
