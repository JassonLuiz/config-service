package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.ConfigHistory;
import io.github.configservice.config_service.repository.ConfigHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConfigHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHistoryService.class);

    private final ConfigHistoryRepository historyRepository;

    public ConfigHistoryService(ConfigHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ConfigHistory recordChange(
            String namespace,
            String environment,
            String configKey,
            String oldValue,
            String newValue,
            String operation,
            String changedBy) {

        String correlationId = getOrGenerateCorrelationId();

        ConfigHistory history = new ConfigHistory(
                namespace,
                environment,
                configKey,
                oldValue,
                newValue,
                operation,
                LocalDateTime.now(),
                changedBy,
                correlationId
        );

        ConfigHistory saved = historyRepository.save(history);

        logger.info("Config change recorded: operation={}, namespace={}, environment={}, key={}, " +
                        "oldValue={}, newValue={}, changedBy={}, correlationId={}",
                operation, namespace, environment, configKey,
                maskSensitiveValue(oldValue),
                maskSensitiveValue(newValue),
                changedBy != null ? changedBy : "SYSTEM",
                correlationId);

        return saved;
    }

    @Transactional
    public ConfigHistory recordChange(
            String namespace,
            String environment,
            String configKey,
            String oldValue,
            String newValue,
            String operation) {

        return recordChange(namespace, environment, configKey, oldValue, newValue, operation, null);
    }

    public Page<ConfigHistory> getConfigHistory(
            String namespace,
            String environment,
            String configKey,
            int page,
            int size) {

        logger.debug("Fetching history: namespace={}, environment={}, key={}, page={}, size={}",
                namespace, environment, configKey, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        Page<ConfigHistory> history = historyRepository.findByNamespaceAndEnvironmentAndConfigKey(
                namespace, environment, configKey, pageable);

        logger.info("Found {} history records for config: namespace={}, environment={}, key={}",
                history.getTotalElements(), namespace, environment, configKey);

        return history;
    }

    public Optional<ConfigHistory> getLastCahnge(String namespace, String environment, String configKey) {
        logger.debug("Fetching last change: namespace={}, environment={}, key={}",
                namespace, environment, configKey);

        Optional<ConfigHistory> lastChange = historyRepository
                .findFirstByNamespaceAndEnvironmentAndConfigKeyOrderByChangedAtDesc(
                        namespace, environment, configKey);

        lastChange.ifPresentOrElse(
                change -> logger.debug("Last change found: operation={}, changedAt={}",
                        change.getOperation(), change.getChangedAt()),
                () -> logger.debug("No history found for config")
        );

        return lastChange;
    }

    public Page<ConfigHistory> getNamespaceHistory(String namespace, int page, int size) {
        logger.debug("Fetching namespace history: namespace={}, page={}, size={}",
                namespace, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return historyRepository.findByNamespace(namespace, pageable);
    }

    public Page<ConfigHistory> getEnvironmentHistory(
            String namespace,
            String environment,
            int page,
            int size) {

        logger.debug("Fetching environment history: namespace={}, environment={}, page={}, size={}",
                namespace, environment, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return historyRepository.findByNamespaceAndEnvironment(namespace, environment, pageable);
    }

    public Page<ConfigHistory> getChangesBetween(
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size) {

        logger.info("Fetching changes between {} and {}", start, end);

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return historyRepository.findByChangedAtBetween(start, end, pageable);
    }

    public List<ConfigHistory> getRecentChanges(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);

        logger.debug("Fetching changes in the last {} hours (since {})", hours, since);

        return historyRepository.findRecentChanges(since);
    }

    public List<ConfigHistory> getDeletedConfigs(String namespace, String environment) {
        logger.debug("Fetching deleted configs: namespace={}, environment={}", namespace, environment);

        List<ConfigHistory> deleted = historyRepository.findDeletedConfigs(namespace, environment);

        logger.info("Found {} deleted configs in namespace={}, environment={}",
                deleted.size(), namespace, environment);

        return deleted;
    }

    public Page<ConfigHistory> getChangesByUser(String changedBy, int page, int size) {
        logger.debug("Fetching changes by user: changedBy={}, page={}, size={}",
                changedBy, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return historyRepository.findByChangedBy(changedBy, pageable);
    }

    public List<ConfigHistory> getChangesByCorrelationId(String correlationId) {
        logger.debug("Fetching changes by correlationId: {}", correlationId);

        List<ConfigHistory> changes = historyRepository.findByCorrelationId(correlationId);

        logger.info("Found {} changes with correlationId={}", changes.size(), correlationId);

        return changes;
    }

    public Map<String, Long> getConfigStatistics(String namespace, String environment, String configKey) {
        logger.debug("Calculating statistics: namespace={}, environment={}, key={}",
                namespace, environment, configKey);

        long totalChanges = historyRepository.countByNamespaceAndEnvironmentAndConfigKey(
                namespace, environment, configKey);

        long creates = historyRepository.countByOperation("CREATE");
        long updates = historyRepository.countByOperation("UPDATE");
        long deletes = historyRepository.countByOperation("DELETE");

        Map<String, Long> stats = Map.of(
                "totalCahnges", totalChanges,
                "creates", creates,
                "updates", updates,
                "deletes", deletes
        );

        logger.info("Config statistics: {}", stats);

        return stats;
    }

    public List<Map<String, Object>> getMostModifiedConfigs(int topN) {
        logger.debug("Fetching top {} most modified configs", topN);

        Pageable pageable = PageRequest.of(0, topN);
        List<Object[]> results = historyRepository.findMostModifiedConfigs(pageable);

        return results.stream()
                .map(row -> Map.<String, Object>of(
                        "namespace", row[0],
                        "environment", row[1],
                        "configKey", row[2],
                        "changeCount", row[3]
                ))
                .collect(Collectors.toList());
    }

    private String getOrGenerateCorrelationId() {
        String correlationId = MDC.get("correlationId");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

    private String maskSensitiveValue(String value) {
        if (value == null) return null;

        String lowerValue = value.toLowerCase();
        if (lowerValue.contains("passwrod") ||
                lowerValue.contains("secret") ||
                lowerValue.contains("token")) {
            return "***REDACTED***";
        }

        if (value.length() > 100) {
            return value.substring(0, 97) + "...";
        }

        return value;
    }
}
