package io.github.clientlibrary.client_library.service;

import io.github.clientlibrary.client_library.dto.ConfigEntryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
public class ConfigApiClient {

    private static final Logger log = LoggerFactory.getLogger(ConfigApiClient.class);

    private final WebClient webClient;

    public ConfigApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ConfigEntryResponseDTO> getAllConfigs(String namespace, String environment) {
        return webClient.get()
                .uri("/api/v1/configs/namespace/{namespace}/env/{environment}", namespace, environment)
                .retrieve()
                .bodyToFlux(ConfigEntryResponseDTO.class)
                .collectList()
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(2))
                                .filter(ex -> ex instanceof WebClientResponseException || ex instanceof RuntimeException)
                                .onRetryExhaustedThrow((retrySpec, signal) -> {
                                    log.error("❌ Retries exhausted while searching '{}:{}'", namespace, environment);
                                    return signal.failure();
                                })
                )
                .onErrorResume(ex -> {
                    log.error("Error fetching all configs: {}", ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }

    public Optional<ConfigEntryResponseDTO> getConfig(String namespace, String environment, String key) {
        return webClient.get()
                .uri("/api/v1/configs/namespace/{namespace}/env/{environment}/key/{key}", namespace, environment, key)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    log.warn("⚠️ Client error {} while fetching config {}:{}:{}",
                            response.statusCode(), namespace, environment, key);
                    return Mono.error(new RuntimeException("Error 4xx in the API"));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    log.error("❌ Server error {} while fetching config {}:{}:{}",
                            response.statusCode(), namespace, environment, key);
                    return Mono.error(new RuntimeException("Erro 5xx na API"));
                })
                .bodyToMono(ConfigEntryResponseDTO.class)
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(2))
                                .filter(ex -> ex instanceof WebClientResponseException || ex instanceof RuntimeException)
                                .onRetryExhaustedThrow((retrySpec, signal) -> {
                                    log.error("❌ Retries exhausted while searching '{}:{}:{}'", namespace, environment, key);
                                    return signal.failure();
                                })
                )
                .onErrorResume(Exception.class, ex -> {
                    log.error("⚠️ Unexpected error fetching config '{}:{}:{}': {}", namespace, environment, key, ex.getMessage());
                    return Mono.empty();
                })
                .blockOptional();
    }
}
