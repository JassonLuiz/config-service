package io.github.clientlibrary.client_library.service.api;

import io.github.configservice.contracts.dto.ConfigEntryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigApiClient {

    private static final Logger log = LoggerFactory.getLogger(ConfigApiClient.class);

    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY_GET_ALL = Duration.ofSeconds(10);
    private static final Duration RETRY_DELAY_GET_CONFIG = Duration.ofSeconds(10);

    private static final String GET_ALL_CONFIGS_URI = "/api/v1/namespaces/{namespaceKey}/environments/{envKey}/configs";
    private static final String GET_CONFIG_URI = "/api/v1/namespaces/{namespaceKey}/environments/{envKey}/configs/{configKey}";

    private final WebClient webClient;

    public ConfigApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ConfigEntryResponseDTO> getAllConfigs(String namespace, String environment) {
        return webClient.get()
                .uri(GET_ALL_CONFIGS_URI, namespace, environment)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    log.warn("⚠️ HTTP error {} while fetching configs for namespace='{}', environment='{}'",
                            response.statusCode(), namespace, environment);
                    return Mono.error(new RuntimeException("HTTP error in the API"));
                })
                .bodyToFlux(ConfigEntryResponseDTO.class)
                .collectList()
                .retryWhen(createRetrySpec(RETRY_DELAY_GET_ALL, namespace, environment, null))
                .onErrorResume(ex -> {
                    log.error("Error fetching all configs: {}", ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }

    public Optional<ConfigEntryResponseDTO> getConfig(String namespace, String environment, String key) {
        return webClient.get()
                .uri(GET_CONFIG_URI, namespace, environment, key)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    log.warn("HTTP error {} while fetching config {}:{}:{}",
                            response.statusCode(), namespace, environment, key);
                    return Mono.error(new RuntimeException("HTTP error in the API"));
                })
                .bodyToMono(ConfigEntryResponseDTO.class)
                .retryWhen(createRetrySpec(RETRY_DELAY_GET_CONFIG, namespace, environment, key))
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error fetching config '{}:{}:{}': {}", namespace, environment, key, ex.getMessage());
                    return Mono.empty();
                })
                .blockOptional();
    }

    private Retry createRetrySpec(Duration delay, String namespace, String environment, String key) {
        String context = key != null ?
                String.format("'%s::%s::%s'", namespace, environment, key) :
                String.format("'%s::%s'", namespace, environment);

        return Retry.fixedDelay(MAX_RETRIES, delay)
                .filter(this::isRetryable)
                .doBeforeRetry(retrySignal ->
                        log.debug("Retrying request for {} (attempt {}/{})",
                                context, retrySignal.totalRetries() + 1, MAX_RETRIES))
                .onRetryExhaustedThrow((retrySpec, signal) -> {
                    log.error("Retries exhausted while searching {}", context);
                    return signal.failure();
                });
    }

    private boolean isRetryable(Throwable ex) {
        if (ex instanceof WebClientResponseException wcre) {
            return wcre.getStatusCode().is5xxServerError() ||
                    wcre.getStatusCode().value() == 429; // Muiltas requisições
        }

        return ex instanceof java.io.IOException
                || ex instanceof java.util.concurrent.TimeoutException
                || ex instanceof java.net.ConnectException
                || isTimeoutException(ex);
    }

    private boolean isTimeoutException(Throwable ex) {
        String className = ex.getClass().getName();
        return className.contains("ReadTimeout") ||
                className.contains("WriteTimeout") ||
                className.contains("ConnectTimeout");
    }
}
