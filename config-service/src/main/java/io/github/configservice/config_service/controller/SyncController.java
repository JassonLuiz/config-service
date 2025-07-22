package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/namespace/{namespace}/env/{env}")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sync-force")
    public ResponseEntity<Void> syncForce(@PathVariable String namespace,
                                          @PathVariable String env) {
        boolean triggered = syncService.forceSync(namespace, env);
        return triggered
                ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();
    }
}
