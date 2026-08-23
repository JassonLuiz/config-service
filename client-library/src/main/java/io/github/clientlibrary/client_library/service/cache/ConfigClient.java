package io.github.clientlibrary.client_library.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.configservice.contracts.dto.ConfigEntryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConfigClient {

    private static final Logger log = LoggerFactory.getLogger(ConfigClient.class);

    private final DynamicConfigHolder holder;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ConfigClient(DynamicConfigHolder holder, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.holder = holder;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String getString(String namespace, String environment, String key) {
        if (!StringUtils.hasText(namespace) || !StringUtils.hasText(environment) || !StringUtils.hasText(key)) {
            log.warn("Invalid parameters: namespace='{}', environment='{}', key='{}'", namespace, environment, key);
            return null;
        }

        String value = holder.get(namespace, environment, key);
        if (value != null) {
            log.debug("Config found in memory cache: {}:{}:{}", namespace, environment, key);
            return value;
        }

        String redisKey = "config:%s:%s:%s".formatted(namespace, environment, key);

        try {
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null) {
                log.debug("Config found in Redis cache: {}:{}:{}", namespace, environment, key);

                try {
                    ConfigEntryResponseDTO dto = objectMapper.readValue(redisValue, ConfigEntryResponseDTO.class);
                    holder.put(namespace, environment, dto.key(), dto.value());
                    return dto.value();
                } catch (Exception e) {
                    log.debug("Redis value is not JSON, using as plain string: {}", redisValue);
                    holder.put(namespace, environment, key, redisValue);
                    return redisValue;
                }
            }
        } catch (Exception e) {
            log.error("Error accessing Redis for key {}: {}", redisKey, e.getMessage());
        }

        log.debug("Config not found: {}:{}:{}", namespace, environment, key);
        return null;
    }

    public boolean getBoolean(String namespace, String environment, String key, boolean defaultValue) {
        String value = getString(namespace, environment, key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public int getInt(String namespace, String environment, String key, int defaultValue) {
        String value = getString(namespace, environment, key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
