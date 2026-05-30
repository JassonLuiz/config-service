package io.github.configservice.config_service.mapper;

import io.github.configservice.config_service.dto.responseDTO.ConfigHistoryResponseDTO;
import io.github.configservice.config_service.model.ConfigHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigHistoryMapper {

    public ConfigHistoryResponseDTO toDTO(ConfigHistory entity) {
        if (entity == null) {
            return null;
        }

        return new ConfigHistoryResponseDTO(
                entity.getId(),
                entity.getNamespace(),
                entity.getEnvironment(),
                entity.getConfigKey(),
                entity.getOldValue(),
                entity.getNewValue(),
                entity.getOperation(),
                entity.getChangedAt(),
                entity.getChangedBy(),
                entity.getCorrelationId(),
                entity.getChangeDescription()
        );
    }

    public List<ConfigHistoryResponseDTO> toDTOList(List<ConfigHistory> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
