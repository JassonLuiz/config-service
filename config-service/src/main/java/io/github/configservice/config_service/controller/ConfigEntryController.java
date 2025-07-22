package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.ConfigEntryBatchDTO;
import io.github.configservice.config_service.dto.ConfigEntryResponseDTO;
import io.github.configservice.config_service.service.ConfigEntryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configs")
public class ConfigEntryController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final ConfigEntryService service;

    public ConfigEntryController(ConfigEntryService service) {
        this.service = service;
    }

    @GetMapping("/namespace/{namespace}/env/{env}")
    public ResponseEntity<List<ConfigEntryResponseDTO>> findAll(@PathVariable String namespace,
                                                                @PathVariable String env) {
        return ResponseEntity.ok(service.getAllByNamespaceAndEnvironment(namespace, env));
    }

    @GetMapping("/namespace/{namespace}/env/{env}/key/{key}")
    public ResponseEntity<ConfigEntryResponseDTO> findOne(@PathVariable String namespace,
                                                          @PathVariable String env,
                                                          @PathVariable String key) {
        return service.getByKey(namespace, env, key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createOrUpdate(@Valid @RequestBody ConfigEntryBatchDTO dto) {
        log.info("Received config batch to create/update");
        service.saveConfigurationTree(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/namespace/{namespace}/env/{env}/key/{key}")
    public ResponseEntity<Void> delete(@PathVariable String namespace,
                                       @PathVariable String env,
                                       @PathVariable String key) {
        boolean deleted = service.deleteConfig(namespace, env, key);
        return deleted ? ResponseEntity.accepted().build() : ResponseEntity.notFound().build();
    }
}
