package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.dto.ConfigEntryDTO;
import io.github.configservice.config_service.model.ConfigEntry;
import io.github.configservice.config_service.service.ConfigEntryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configs")
public class ConfigEntryController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryController.class);
    private final ConfigEntryService service;

    public ConfigEntryController(ConfigEntryService service) {
        this.service = service;
    }

    @PostMapping
    public ConfigEntry create(@Valid @RequestBody ConfigEntryDTO dto){
        log.info("Request to create/update configuration received: {}", dto);

        ConfigEntry config = new ConfigEntry();
        config.setNamespace(dto.namespace());
        config.setEnvironment(dto.environment());
        config.setKeyName(dto.keyName());
        config.setValue(dto.value());

        ConfigEntry result = service.create(config);
        log.info("Configuration persisted successfully: {}", result);
        return result;
    }

    @GetMapping
    public ConfigEntry findById(@RequestParam Long id) {
        log.info("Request to get configuration by ID: {}", id);
        return service.findById(id);
    }
}
