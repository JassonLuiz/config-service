package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.createDTO.NamespaceCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.NamespaceResponseDTO;
import io.github.configservice.config_service.service.NamespaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/namespace")
public class NamespaceController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final NamespaceService namespaceService;

    public NamespaceController(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @GetMapping
    public ResponseEntity<List<NamespaceResponseDTO>> findAll() {
        return ResponseEntity.ok(namespaceService.findAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<NamespaceResponseDTO> findByKey(@PathVariable String key) {
        return namespaceService.findByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NamespaceResponseDTO> create(@RequestBody NamespaceCreateDTO dto) {
        log.info("Creating namespace with key='{}'", dto.key());
        NamespaceResponseDTO created = namespaceService.createNamespace(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
