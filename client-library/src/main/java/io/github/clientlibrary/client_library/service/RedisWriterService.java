package io.github.clientlibrary.client_library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clientlibrary.client_library.dto.ConfigEntryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisWriterService {

    private static final Logger log = LoggerFactory.getLogger(RedisWriterService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisWriterService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void write(String namespace, String environment, ConfigEntryResponseDTO dto) {
        try {
            String redisKey = "config:%s:%s:%s".formatted(namespace, environment, dto.key());
            String jsonValue = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(redisKey, jsonValue);
            log.debug("✅ Recorded Redis: {} = {}", redisKey, jsonValue);
        } catch (Exception e) {
            log.error("❌ Error writing to Redis: {}", e.getMessage(), e);
        }
    }

    public void writerAll(String namespace, String environment, List<ConfigEntryResponseDTO> configs) {
        configs.forEach(dto -> write(namespace, environment, dto));
    }

    public void delete(String namespace, String environment, String key) {
        String redisKey = "config:%s:%s:%s".formatted(namespace, environment, key);
        redisTemplate.delete(redisKey);
    }
}
