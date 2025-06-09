package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.model.ConfigEntryHistory;
import io.github.configservice.config_service.service.ConfigEntryHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/config/history")
public class ConfigEntryHistoryController {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntryHistoryController.class);
    private final ConfigEntryHistoryService service;

    public ConfigEntryHistoryController(ConfigEntryHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{configEntryId}")
    public List<ConfigEntryHistory> getHistory(Long configEntryId) {
        log.info("Request to get history for configuration ID: {}", configEntryId);
        return service.getHistoryByConfig(configEntryId);
    }
}
