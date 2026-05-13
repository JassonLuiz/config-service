package io.github.clientlibrary.client_library.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clientlibrary.client_library.dto.ConfigEntryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RedisWriterService {

    private static final Logger log = LoggerFactory.getLogger(RedisWriterService.class);
    private static final String REDIS_KEY_PATTERN = "config:%s:%s:%s";
    private static final Duration EXPIRATION = Duration.ofDays(30);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DynamicConfigHolder holder;

    public RedisWriterService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, DynamicConfigHolder holder) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.holder = holder;
    }

    public boolean write(String namespace, String environment, ConfigEntryResponseDTO dto) {
        if (!validateWriteParameters(namespace, environment, dto)) {
            return false;
        }

        String redisKey = buildRedisKey(namespace, environment, dto.key());

        try {
            String jsonValue = serializeConfig(dto);
            redisTemplate.opsForValue().set(redisKey, jsonValue);
            holder.put(namespace, environment, dto.key(), dto.value());
            log.debug("Config written to Redis: key='{}'", redisKey);
            return true;
        } catch (Exception e) {
            log.error("Error writing to Redis: {}", e.getMessage(), e);
        }

        return false;
    }

    public int writerAll(String namespace, String environment, List<ConfigEntryResponseDTO> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.warn("Attempted to write empty configs list to Redis");
            return 0;
        }

        if (!validateBatchWriteParameters(namespace, environment, configs)) {
            return 0;
        }

        log.info("Starting batch write operation for {} configs", configs.size());

        AtomicInteger successCount = new AtomicInteger();
        
        try {
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    @SuppressWarnings("unchecked")
                    var ops = (RedisOperations<String, String>) operations;

                    configs.forEach(dto -> {
                        try {
                            String redisKey = buildRedisKey(namespace, environment, dto.key());
                            String jsonValue = serializeConfig(dto);
                            ops.opsForValue().set(redisKey, jsonValue);
                            successCount.getAndIncrement();
                        } catch (JsonProcessingException e) {
                            log.error("Error serializing config in batch: key='{}'",
                                    dto.key(), e);
                        }
                    });
                    return null;
                }
            });

            updateLocalCache(namespace, environment, configs);

            log.info("Write operation completed: {}/{} configs written to Redis successfully",
                    successCount.get(), configs.size());
        } catch (Exception e) {
            log.error("Unexpected error in batch write: namespace='{}', environment='{}': {}",
                    namespace, environment, e.getMessage(), e);
            return fallbackIndividualWrites(namespace, environment, configs);
        }

        return successCount.get();
    }

    public boolean delete(String namespace, String environment, String key) {
        if (!validateDeleteParameters(namespace, environment, key)) {
            return false;
        }

        String redisKey = buildRedisKey(namespace, environment, key);

        try {
            boolean deleted = redisTemplate.delete(redisKey);
            holder.remove(namespace, environment, key);
            log.debug("Deleted from Redis and removed from local cache: {}", redisKey);
            return true;
        } catch (Exception e) {
            log.error("Error deleting from Redis: key='{}': {}", redisKey, e.getMessage(), e);

            try {
                holder.remove(namespace, environment, key);
            } catch (Exception localEx) {
                log.warn("Failed to remove from local cache: key='{}': {}", redisKey, localEx.getMessage());
            }
            return false;
        }
    }

    public int deleteAll(String namespace, String environment, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            log.warn("Attempted to delete empty keys list from Redis");
            return 0;
        }

        if (!validateBatchDeleteParameters(namespace, environment, keys)) {
            return 0;
        }

        log.info("Starting delete operation for {} config(s)", keys.size());

        try {
            Set<String> redisKeys = keys.stream()
                    .map(key -> buildRedisKey(namespace, environment, key))
                    .collect(Collectors.toSet());

            Long deletedCount = redisTemplate.delete(redisKeys);

            keys.forEach(key -> {
                try {
                    holder.remove(namespace, environment, key);
                } catch (Exception e) {
                    log.warn("Failed to remove from local cache: key='{}': {}", key, e.getMessage());
                }
            });

            int actualDeleted = deletedCount != null ? deletedCount.intValue() : 0;
            log.info("Batch delete completed successfully: {} configs deleted from Redis", actualDeleted);
            return actualDeleted;
        } catch (Exception e) {
            log.error("Unexpected error in batch delete: namespace='{}', environment='{}': {}",
                    namespace, environment, e.getMessage(), e);
            return fallbackIndividualDeletes(namespace, environment, keys);
        }
    }

    private int fallbackIndividualWrites(String namespace, String environment, List<ConfigEntryResponseDTO> configs) {
        log.info("Falling back to individual writes for {} configs", configs.size());
        int successCount = 0;

        for (ConfigEntryResponseDTO config : configs) {
            try {
                String redisKey = buildRedisKey(namespace, environment, config.key());
                String jsonValue = serializeConfig(config);
                redisTemplate.opsForValue().set(redisKey, jsonValue, EXPIRATION);
                holder.put(namespace, environment, config.key(), config.value());
                successCount++;
                log.debug("Individual fallback write successful: key='{}'", redisKey);
            } catch (Exception e) {
                log.error("Individual fallback write failed: key='{}': {}", config.key(), e.getMessage());
            }
        }

        log.info("Fallback individual writes completed: {}/{} successful", successCount, configs.size());
        return successCount;
    }

    private int fallbackIndividualDeletes(String namespace, String environment, List<String> keys) {
        log.info("Falling back to individual deletes for {} keys", keys.size());
        int successCount = 0;

        for (String key : keys) {
            try {
                String redisKey = buildRedisKey(namespace, environment, key);
                boolean deleted = redisTemplate.delete(redisKey);
                holder.remove(namespace, environment, key);
                successCount++;
                log.debug("Individual fallback delete successful: key='{}'", redisKey);
            } catch (Exception e) {
                log.error("Individual fallback delete failed: key='{}': {}", key, e.getMessage());
                try {
                    holder.remove(namespace, environment, key);
                } catch (Exception localEx) {
                    log.warn("Failed to remove from local cache during fallback: key='{}': {}", key, localEx.getMessage());
                }
            }
        }

        log.info("Fallback individual deletes completed: {}/{} successful", successCount, keys.size());
        return successCount;
    }

    private String buildRedisKey(String namespace, String environment, String key) {
        return REDIS_KEY_PATTERN.formatted(namespace, environment, key);
    }

    private String serializeConfig(ConfigEntryResponseDTO dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    private void updateLocalCache(String namespace, String environment, List<ConfigEntryResponseDTO> configs) {
        configs.forEach(dto -> {
            try {
                holder.put(namespace, environment, dto.key(), dto.value());
            } catch (Exception e) {
                log.error("Error updating local cache for key {}: {}", dto.key(), e.getMessage());
            }
        });
    }

    private boolean validateWriteParameters(String namespace, String environment, ConfigEntryResponseDTO dto) {
        if (namespace == null || namespace.trim().isEmpty()) {
            log.warn("Invalid write parameters: namespace is null or empty");
            return false;
        }
        if (environment == null || environment.trim().isEmpty()) {
            log.warn("Invalid write parameters: environment is null or empty");
            return false;
        }
        if (dto == null) {
            log.warn("Invalid write parameters: ConfigEntryResponseDTO is null");
            return false;
        }
        if (dto.key() == null || dto.key().trim().isEmpty()) {
            log.warn("Invalid write parameters: config key is null or empty");
            return false;
        }
        return true;
    }

    private boolean validateBatchWriteParameters(String namespace, String environment, List<ConfigEntryResponseDTO> configs) {
        if (namespace == null || namespace.trim().isEmpty()) {
            log.warn("Invalid batch write parameters: namespace is null or empty");
            return false;
        }
        if (environment == null || environment.trim().isEmpty()) {
            log.warn("Invalid batch write parameters: environment is null or empty");
            return false;
        }
        if (CollectionUtils.isEmpty(configs)) {
            log.warn("Invalid batch write parameters: configs list is null or empty");
            return false;
        }

        for (ConfigEntryResponseDTO dto : configs) {
            if (dto == null || dto.key() == null || dto.key().trim().isEmpty()) {
                log.warn("Invalid batch write parameters: found config with invalid key");
                return false;
            }
        }
        return true;
    }

    private boolean validateDeleteParameters(String namespace, String environment, String key) {
        if (namespace == null || namespace.trim().isEmpty()) {
            log.warn("Invalid delete parameters: namespace is null or empty");
            return false;
        }
        if (environment == null || environment.trim().isEmpty()) {
            log.warn("Invalid delete parameters: environment is null or empty");
            return false;
        }
        if (key == null || key.trim().isEmpty()) {
            log.warn("Invalid delete parameters: config key is null or empty");
            return false;
        }
        return true;
    }

    private boolean validateBatchDeleteParameters(String namespace, String environment, List<String> keys) {
        if (namespace == null || namespace.trim().isEmpty()) {
            log.warn("Invalid batch delete parameters: namespace is null or empty");
            return false;
        }
        if (environment == null || environment.trim().isEmpty()) {
            log.warn("Invalid batch delete parameters: environment is null or empty");
            return false;
        }
        if (CollectionUtils.isEmpty(keys)) {
            log.warn("Invalid batch delete parameters: keys list is null or empty");
            return false;
        }

        for (String key : keys) {
            if (key == null || key.trim().isEmpty()) {
                log.warn("Invalid batch delete parameters: found null or empty key");
                return false;
            }
        }
        return true;
    }

}
