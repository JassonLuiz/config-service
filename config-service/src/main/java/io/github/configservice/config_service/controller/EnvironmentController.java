package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.createDTO.EnvironmentCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.EnvironmentResponseDTO;
import io.github.configservice.config_service.service.EnvironmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespaces/{namespaceKey}/environments")
public class EnvironmentController {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentController.class);
    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping
    public ResponseEntity<List<EnvironmentResponseDTO>> findAllByNamespace(@PathVariable String namespaceKey) {
        return ResponseEntity.ok(environmentService.findAllByNamespace(namespaceKey));
    }

    @GetMapping("/{key}")
    public ResponseEntity<EnvironmentResponseDTO> findByKey(@PathVariable String namespaceKey,
                                                            @PathVariable String envKey) {
        return environmentService.findByKeyAndNamespace(envKey, namespaceKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EnvironmentResponseDTO> create(@PathVariable String namespaceKey,
                                                         @Valid @RequestBody EnvironmentCreateDTO dto) {
        log.info("Creating environment with key='{}' for namespace='{}'", dto.key(), namespaceKey);
        EnvironmentResponseDTO created = environmentService.createEnvironment(dto, namespaceKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{envKey}")
    public ResponseEntity<EnvironmentResponseDTO> update(@PathVariable String namespaceKey,
                                                         @PathVariable String envKey,
                                                         @Valid @RequestBody EnvironmentCreateDTO dto) {
        log.info("Updating environment with key='{}' for namespace='{}'", envKey, namespaceKey);
        return environmentService.update(namespaceKey, envKey, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    //Add metodo delete
}
