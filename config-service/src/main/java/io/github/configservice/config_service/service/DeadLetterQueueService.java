package io.github.configservice.config_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.configservice.config_service.event.ConfigEvent;
import io.github.configservice.config_service.constants.DLQStatus;
import io.github.configservice.config_service.model.DeadLetterMessage;
import io.github.configservice.config_service.repository.DeadLetterQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DeadLetterQueueService {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueueService.class);

    private static final int MAX_RETRIES = 3;
    private static final int KAFKA_TIMEOUT_SECONDS = 5;

    private final DeadLetterQueueRepository dlqRepository;
    private final KafkaTemplate<String, ConfigEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DeadLetterQueueService(DeadLetterQueueRepository dlqRepository, KafkaTemplate<String, ConfigEvent> kafkaTemplate, ObjectMapper objectMapper) {
        this.dlqRepository = dlqRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public DeadLetterMessage saveFailedEvent(String topic, ConfigEvent event, String errorMessage) {
        try {
            String eventPayload = objectMapper.writeValueAsString(event);
            DeadLetterMessage dlq = new DeadLetterMessage(
                    eventPayload,
                    topic,
                    errorMessage,
                    LocalDateTime.now(),
                    event.correlationId(),
                    event.type().name(),
                    event.namespace(),
                    event.environment(),
                    event.getConfigCount()
            );

            DeadLetterMessage saved = dlqRepository.save(dlq);

            logger.info("Event saved to DLQ: id={}, topic={}, correlationId={}, eventType={}, configCount={}",
                    saved.getId(), topic, event.correlationId(), event.type(), event.getConfigCount());

            return saved;
        } catch (JsonProcessingException e) {
            logger.error("CRITICAL: Failed to serialize event to JSON! Event might be lost. " +
                            "Topic={}, CorrelationId={}, EventType={}, Error={}",
                    topic, event.correlationId(), event.type(), e.getMessage(), e);

            try {
                DeadLetterMessage dlq = new DeadLetterMessage();
                dlq.setEventPayload("SERIALIZATION_FAILED: " + e.getMessage());
                dlq.setTargetTopic(topic);
                dlq.setErrorMessage("Failed to serialize event: " + e.getMessage());
                dlq.setFailedAt(LocalDateTime.now());
                dlq.setCorrelationId(event.correlationId());
                dlq.setEventType(event.type().name());
                dlq.setNamespace(event.namespace());
                dlq.setEnvironment(event.environment());
                dlq.setConfigCount(event.getConfigCount());

                return dlqRepository.save(dlq);
            } catch (Exception fallbackError) {
                logger.error("CRITICAL: Failed to save even fallback DLQ record!", fallbackError);
                return null;
            }
        } catch (Exception e) {
            logger.error("CRITICAL: Failed to save event to DLQ! Event might be lost. " +
                            "Topic={}, Event={}, Error={}",
                    topic, event, e.getMessage(), e);

            return null;
        }
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processDeadLetterQueue() {
        logger.debug("Starting DLQ processing job");

        try {
            int unstuck = resetStuckEvents();
            if (unstuck > 0) {
                logger.warn("Reset {} stuck RETRYING events to PENDING", unstuck);
            }

            List<DeadLetterMessage> pendingEvents = dlqRepository.findPendingForRetry(MAX_RETRIES);

            if (pendingEvents.isEmpty()) {
                logger.debug("No pending events in DLQ");
            }

            logger.info("Processing {} events from DLQ", pendingEvents.size());

            int successCount = 0;
            int failedCount = 0;
            int permanentFailCount = 0;

            for (DeadLetterMessage dlq : pendingEvents) {
                RetryResult result = retryResult(dlq);

                switch (result) {
                    case SUCCESS -> successCount++;
                    case FAILED_RETRY_AGAIN -> failedCount++;
                    case FAILED_PERMANENT -> permanentFailCount++;
                }
            }

            logger.info("DLQ processing completed: Success={}, Failed={}, PermanentFail={}, Total={}",
                    successCount, failedCount, permanentFailCount, pendingEvents.size());

            cleanupOldRecords();
        } catch (Exception e) {
            logger.error("Error processing DLQ", e);
        }
    }

    private RetryResult retryResult(DeadLetterMessage dlq) {
        try {
            dlq.markAsRetrying();
            dlqRepository.save(dlq);

            logger.debug("Retrying event: id={}, topic={}, correlationId={}, attempt={}",
                    dlq.getId(), dlq.getTargetTopic(), dlq.getCorrelationId(), dlq.getRetryCount() + 1);

            ConfigEvent event = objectMapper.readValue(dlq.getEventPayload(), ConfigEvent.class);

            kafkaTemplate.send(dlq.getTargetTopic(), event)
                    .get(KAFKA_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            dlq.markAsResolved();
            dlqRepository.delete(dlq);

            logger.info("Event successfully republished: id={}, topic={}, correlationId={}",
                    dlq.getId(), dlq.getTargetTopic(), dlq.getCorrelationId());

            return RetryResult.SUCCESS;
        } catch (TimeoutException e) {
            logger.warn("Kafka error while retrying event: id={}, topic={}, error={}",
                    dlq.getId(), dlq.getTargetTopic(), e.getCause().getMessage());

            return handleRetryFailure(dlq, "Kafka timeout: " + e.getMessage());
        } catch (ExecutionException e) {
            logger.warn("Kafka error while retrying event: id={}, topic={}, error={}",
                    dlq.getId(), dlq.getTargetTopic(), e.getCause().getMessage());

            return handleRetryFailure(dlq, "Kafka error: " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Retry interrupted for event: id={}", dlq.getId());

            dlq.setStatus(DLQStatus.PENDING);
            dlqRepository.save(dlq);

            return RetryResult.FAILED_RETRY_AGAIN;
        } catch (JsonProcessingException e) {
            logger.error("Invalid JSON payload in DLQ event: id={}, error={}",
                    dlq.getId(), e.getMessage());

            dlq.markAsFailed();
            dlq.setErrorMessage("Invalid JSON: " + e.getMessage());
            dlqRepository.save(dlq);

            return RetryResult.FAILED_PERMANENT;
        } catch (Exception e) {
            logger.error("Unexpected error retrying event: id={}, error={}",
                    dlq.getId(), e.getMessage(), e);

            return handleRetryFailure(dlq, "Unexpected error: " + e.getMessage());
        }

    }

    private RetryResult handleRetryFailure(DeadLetterMessage dlq, String errorMessage) {
        dlq.incrementRetryCount();
        dlq.setErrorMessage(errorMessage);

        if (dlq.hasExceededMaxRetries()) {
            dlq.markAsFailed();
            dlqRepository.save(dlq);

            logger.error("Event permanently failed after {} retries: id={}, topic={}, correlationId={}",
                    MAX_RETRIES, dlq.getId(), dlq.getTargetTopic(), dlq.getCorrelationId());

            return RetryResult.FAILED_PERMANENT;
        } else {
            dlq.setStatus(DLQStatus.PENDING);
            dlqRepository.save(dlq);

            logger.debug("Event will be retried again: id={}, retryCount={}/{}",
                    dlq.getId(), dlq.getRetryCount(), MAX_RETRIES);

            return RetryResult.FAILED_RETRY_AGAIN;
        }
    }

    private int resetStuckEvents() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        return dlqRepository.resetStuckRetrying(tenMinutesAgo);
    }

    private void cleanupOldRecords() {
        try {
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            int deletedResolved = dlqRepository.deleteByStatusAndFailedAtBefore(
                    DLQStatus.RESOLVED, sevenDaysAgo);

            if (deletedResolved > 0) {
                logger.info("Cleaned up {} old RESOLVED DLQ records", deletedResolved);
            }

            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            int deletedFailed = dlqRepository.deleteFailedOlderThan(thirtyDaysAgo);

            if (deletedFailed > 0 ) {
                logger.warn("Cleaned up {} old FAILED DLQ records (>30 days)", deletedFailed);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up old DLQ records", e);
        }
    }

    public Page<DeadLetterMessage> getPendingEvents(Pageable pageable) {
        return dlqRepository.findByStatus(DLQStatus.PENDING, pageable);
    }

    public Page<DeadLetterMessage> getFailedEvents(Pageable pageable) {
        return dlqRepository.findByStatus(DLQStatus.FAILED, pageable);
    }

    public List<DeadLetterMessage> getEventsByNamespace(String namespace) {
        return dlqRepository.findByNamespace(namespace);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("pendingCount", dlqRepository.countByStatus(DLQStatus.PENDING));
        stats.put("failedCount", dlqRepository.countByStatus(DLQStatus.FAILED));
        stats.put("retryingCount", dlqRepository.countByStatus(DLQStatus.RETRYING));
        stats.put("resolvedCount", dlqRepository.countByStatus(DLQStatus.RESOLVED));
        stats.put("totalCount", dlqRepository.count());

        return stats;
    }

    @Transactional
    public boolean forceRetry(Long eventId) {
        return dlqRepository.findById(eventId)
                .map(dlq -> {
                    if (dlq.getStatus().equals(DLQStatus.FAILED) ||
                        dlq.getStatus().equals(DLQStatus.PENDING)) {

                        dlq.setStatus(DLQStatus.PENDING);
                        dlq.setRetryCount(0);
                        dlqRepository.save(dlq);

                        logger.info("Event {} reset for retry by admin", eventId);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public Optional<DeadLetterMessage> getEventById(Long eventId) {
        return dlqRepository.findById(eventId);
    }

    public int retryAllFailed() {
        List<DeadLetterMessage> failedEvents = dlqRepository.findByStatus(DLQStatus.FAILED,
                PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        int count = 0;
        for (DeadLetterMessage dlq : failedEvents) {
            dlq.setStatus(DLQStatus.PENDING);
            dlq.setRetryCount(0);
            dlqRepository.save(dlq);
            count++;
        }

        logger.info("Reset {} FAILED events to PENDING for retry", count);
        return count;
    }

    @Async
    public void processDeadLetterQueueAsync() {
        logger.info("Processing DLQ triggered manually by admin");
        try {
            processDeadLetterQueue();
        } catch (Exception e) {
            logger.error("Error in async DLQ processing", e);
            throw e;
        }
    }

    private enum RetryResult {
        SUCCESS,
        FAILED_RETRY_AGAIN,
        FAILED_PERMANENT
    }
}
