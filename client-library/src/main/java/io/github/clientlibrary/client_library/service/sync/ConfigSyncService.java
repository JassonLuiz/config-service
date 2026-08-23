package io.github.clientlibrary.client_library.service.sync;

import io.github.clientlibrary.client_library.config.ConfigValueInjector;
import io.github.configservice.contracts.dto.ConfigEntryResponseDTO;
import io.github.configservice.contracts.event.ConfigEvent;
import io.github.clientlibrary.client_library.service.api.ConfigApiClient;
import io.github.clientlibrary.client_library.service.cache.RedisWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigSyncService {

    private static final Logger log = LoggerFactory.getLogger(ConfigSyncService.class);

    private final ConfigApiClient apiClient;
    private final RedisWriterService redisWriter;
    private final ConfigValueInjector injector;

    public ConfigSyncService(ConfigApiClient apiClient, RedisWriterService redisWriter, ConfigValueInjector injector) {
        this.apiClient = apiClient;
        this.redisWriter = redisWriter;
        this.injector = injector;
    }

    public void handleSync(String namespace, String environment) {
        log.info("📦 SYNC event received for namespace='{}', environment='{}'", namespace, environment);

        var configs = apiClient.getAllConfigs(namespace, environment);
        log.info("🔍 Found {} config(s) in the API for sync", configs.size());

        redisWriter.writerAll(namespace, environment, configs);
        injector.refreshAll();
        log.info("✅ All configurations saved to Redis for namespace='{}', environment='{}'", namespace, environment);
    }

    public boolean processConfigEvent(ConfigEvent event) {
        if (!event.hasConfigKeys()) {
            log.warn("Event received with empty config keys list. Skipping processing.");
            return false;
        }

        if (!validateParameters(event)) {
            return false;
        }

        log.info("Processing config event: type={}, configCount={}, namespace={}, environment={}, correlationId={}",
                event.type(), event.getConfigCount(), event.namespace(), event.environment(), event.correlationId());

        try {
            switch (event.type()) {
                case CREATE, UPDATE -> {
                    return handleCreateUpdate(event);
                }
                case DELETE -> {
                    return handleDelete(event);
                }
                default -> {
                    log.warn("Unknown event type: {}", event.type());
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Error processing config event: correlationId={}, error={}", event.correlationId(), e.getMessage(), e);
            return false;
        }
    }

    public boolean handleCreateUpdate(ConfigEvent event) {
        try {
            List<ConfigEntryResponseDTO> foundConfigs = new ArrayList<>();

            for (String key : event.configKeys()) {
                try {
                    Optional<ConfigEntryResponseDTO> configOpt = apiClient.getConfig(
                            event.namespace(), event.environment(), key);


                    if (configOpt != null || configOpt.isPresent()) {
                        foundConfigs.add(configOpt.get());
                        log.trace("Found config in API: key='{}'", key);
                    } else {
                        log.trace("Config not found in API: key='{}'", key);
                    }
                } catch (Exception e) {
                    log.warn("Error fetching config '{}' from API: {}", key, e.getMessage());
                }
            }

            if (foundConfigs.isEmpty()) {
                log.warn("No valid configurations found in API for event: correlationId={}", event.correlationId());
                return true;
            }

            int writtenCount = redisWriter.writerAll(event.namespace(), event.environment(), foundConfigs);

            boolean refreshSuccess = refreshConfigs();
            boolean success = writtenCount > 0 && refreshSuccess;

            if (event.isSingleConfig()) {
                if (success) {
                    log.info("Single CREATE/UPDATE completed successfully: key={}, correlationId={}",
                            event.getSingleConfigKey(), event.correlationId());
                } else {
                    log.warn("Single CREATE/UPDATE completed with issues: key={}, correlationId={}",
                            event.getSingleConfigKey(), event.correlationId());
                }
            } else {
                log.info("Batch CREATE/UPDATE completed: {}/{} configs processed, {}/{} found in API, correlationId={}",
                        writtenCount, event.getConfigCount(), foundConfigs.size(), event.getConfigCount(), event.correlationId());
            }

            return success;
        } catch (Exception e) {
            log.error("Error processing CREATE/UPDATE event: correlationId={}, error={}", event.correlationId(), e.getMessage(), e);
            return false;
        }
    }

    private boolean handleDelete(ConfigEvent event) {
        try {
            int deletedCount = redisWriter.deleteAll(event.namespace(), event.environment(), event.configKeys());

            boolean refreshSuccess = refreshConfigs();
            boolean success = deletedCount >= 0 && refreshSuccess;

            if (event.isSingleConfig()) {
                if (success) {
                    log.info("Single DELETE completed successfully: key={}, correlationId={}",
                            event.getSingleConfigKey(), event.correlationId());
                } else {
                    log.warn("Single DELETE completed with issues: key={}, correlationId={}",
                            event.getSingleConfigKey(), event.correlationId());
                }
            } else {
                log.info("Batch DELETE completed: {}/{} configs deleted, correlationId={}",
                        deletedCount, event.getConfigCount(), event.correlationId());
            }

            return success;

        } catch (Exception e) {
            log.error("Error in DELETE processing: correlationId={}, error={}", event.correlationId(), e.getMessage(), e);
            return false;
        }
    }

    private boolean validateParameters(ConfigEvent event) {
        if (event.namespace() == null || event.namespace().trim().isEmpty()) {
            log.warn("Event validation failed: namespace is null or empty");
            return false;
        }
        if (event.environment() == null || event.environment().trim().isEmpty()) {
            log.warn("Event validation failed: environment is null or empty");
            return false;
        }
        if (CollectionUtils.isEmpty(event.configKeys())) {
            log.warn("Event validation failed: configKeys list is null or empty");
            return false;
        }

        for (String key : event.configKeys()) {
            if (key == null || key.trim().isEmpty()) {
                log.warn("Event validation failed: found null or empty config key");
                return false;
            }
        }
        return true;
    }

    private boolean refreshConfigs() {
        try {
            injector.refreshAll();
            log.debug("Configuration refresh completed");
            return true;
        } catch (Exception e) {
            log.error("Error refreshing configurations: {}", e.getMessage(), e);
            return false;
        }
    }
}
