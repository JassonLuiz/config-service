package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.ConfigEntryBatchDTO;
import io.github.configservice.config_service.dto.createDTO.ConfigEntryCreateDTO;
import io.github.configservice.contracts.dto.ConfigEntryResponseDTO;
import io.github.configservice.config_service.service.ConfigEntryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespaces/{namespaceKey}/environments/{envKey}/configs")
public class ConfigEntryController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final ConfigEntryService configEntryService;

    public ConfigEntryController(ConfigEntryService configEntryService) {
        this.configEntryService = configEntryService;
    }

    @GetMapping
    public ResponseEntity<List<ConfigEntryResponseDTO>> findAll(@PathVariable String namespaceKey,
                                                                @PathVariable String envKey) {
        return ResponseEntity.ok(configEntryService.getAllByNamespaceAndEnvironment(namespaceKey, envKey));
    }

    @GetMapping("/{configKey}")
    public ResponseEntity<ConfigEntryResponseDTO> findByKey(@PathVariable String namespaceKey,
                                                            @PathVariable String envKey,
                                                            @PathVariable String configKey) {
        return configEntryService.getByKey(namespaceKey, envKey, configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<List<ConfigEntryResponseDTO>> create(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @Valid @RequestBody List<ConfigEntryCreateDTO> dto) {
        log.info("Creating config with key='{}' for namespace='{}', environment='{}'",
                dto.size(), namespaceKey, envKey);
        List<ConfigEntryResponseDTO> created = configEntryService.createConfiguration(namespaceKey, envKey, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{configKey}")
    public ResponseEntity<ConfigEntryResponseDTO> update(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @PathVariable String configKey,
                                                         @Valid @RequestBody ConfigEntryCreateDTO dto) {
        log.info("Updating config with key='{}' for namespace='{}', environment='{}'",
                configKey, namespaceKey, envKey);
        return configEntryService.updateConfiguration(namespaceKey, envKey, configKey, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{configKey}")
    public ResponseEntity<Void> delete(@PathVariable String namespaceKey,
                                       @PathVariable String envKey,
                                       @PathVariable String configKey) {
        boolean deleted = configEntryService.deleteConfiguration(namespaceKey, envKey, configKey);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
