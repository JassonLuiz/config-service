package io.github.clientlibrary.client_library.service;

import io.github.clientlibrary.client_library.consumer.ConfigChangedEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConfigSyncService {

    private static final Logger log = LoggerFactory.getLogger(ConfigSyncService.class);

    private final ConfigApiClient apiClient;
    private final RedisWriterService redisWriter;

    public ConfigSyncService(ConfigApiClient apiClient, RedisWriterService redisWriter) {
        this.apiClient = apiClient;
        this.redisWriter = redisWriter;
    }

    public void handleSync(String namespace, String environment) {
        log.info("📦 SYNC event received for namespace='{}', environment='{}'", namespace, environment);

        var configs = apiClient.getAllConfigs(namespace, environment);
        log.info("🔍 Found {} config(s) in the API for sync", configs.size());

        redisWriter.writerAll(namespace, environment, configs);
        log.info("✅ All configurations saved to Redis for namespace='{}', environment='{}'", namespace, environment);
    }

    public void handleUpdate(String namespace, String environment, String key) {
        log.info("🔁 Update received for key='{}' in the namespace='{}', environment='{}'", key, namespace, environment);
        apiClient.getConfig(namespace, environment, key)
                .ifPresent(dto -> {
                    log.debug("📥 Configuration found in the API: {}", dto);
                    try {
                        redisWriter.write(namespace, environment, dto);
                    } catch (Exception e) {
                        log.error("❌ Error writing to Redis: {}", e.getMessage(), e);
                    }
                    log.info("✅ Configuration updated in Redis successfully.");
                });
    }

    public void handleDelete(String namespace, String environment, String key) {
        redisWriter.delete(namespace, environment, key);
    }
}
