package io.github.configservice.config_service.controller;

import io.github.configservice.config_service.model.DLQStatus;
import io.github.configservice.config_service.model.DeadLetterMessage;
import io.github.configservice.config_service.service.DeadLetterQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/admin/dlq")
public class DeadLetterQueueController {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueueController.class);

    private final DeadLetterQueueService dlqService;

    public DeadLetterQueueController(DeadLetterQueueService dlqService) {
        this.dlqService = dlqService;
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<DeadLetterMessage>> getPendingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "failedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        logger.debug("Fetching pending DLQ events: page={}, size={}, sort={}, direction={}",
                page, size, sort, direction);

        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<DeadLetterMessage> events = dlqService.getPendingEvents(pageable);

            logger.info("Fetched {} pending DLQ events (page {}/{})",
                    events.getNumberOfElements(), page + 1, events.getTotalPages());

            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid sort direction: {}", direction);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/failed")
    public ResponseEntity<Page<DeadLetterMessage>> getFailedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "failedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        logger.debug("Fetching failed DLQ events: page={}, size={}", page, size);

        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<DeadLetterMessage> events = dlqService.getFailedEvents(pageable);

            logger.info("Fetched {} failed DLQ events (page {}/{})",
                    events.getNumberOfElements(), page + 1, events.getTotalPages());

            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid sort direction: {}", direction);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/namespace/{namespace}")
    public ResponseEntity<List<DeadLetterMessage>> getEventsByNamespace(
            @PathVariable String namespace) {

        logger.debug("Fetching DLQ events for namespace: {}", namespace);

        List<DeadLetterMessage> events = dlqService.getEventsByNamespace(namespace);

        logger.info("Fetched {} DLQ events for namespace: {}", events.size(), namespace);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
        logger.debug("Fetching DLQ event by ID: {}", eventId);

        return dlqService.getEventById(eventId)
                .<ResponseEntity<?>>map(event -> {
                    logger.info("Found DLQ event: id={}, status={}", eventId, event.getStatus());
                    return ResponseEntity.ok(event);
                })
                .orElseGet(() -> {
                    logger.warn("DLQ event not found: id={}", eventId);

                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Event not found");
                    error.put("eventId", eventId);

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.debug("Fetching DLQ statistics");

        Map<String, Long> stats = dlqService.getStatistics();

        Map<String, Object> response = new HashMap<>(stats);
        response.put("timestamp", java.time.LocalDateTime.now());

        logger.info("DLQ Statistics: pending={}, failed={}, total={}",
                stats.get("pendingCount"), stats.get("failedCount"), stats.get("totalCount"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        logger.debug("Checking DLQ health");

        Map<String, Long> stats = dlqService.getStatistics();
        long failedCount = stats.get("failedCount");
        long pendingCount = stats.get("pendingCount");

        Map<String, Object> health = new HashMap<>();
        health.put("failedCount", failedCount);
        health.put("pendingCount", pendingCount);

        String status;
        String message;
        HttpStatus httpStatus;

        if (failedCount >= 50) {
            status = "unhealthy";
            message = "DLQ has high number of failed events - requires attention";
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            logger.error("DLQ health: UNHEALTHY - {} failed events", failedCount);
        } else if (failedCount >= 10) {
            status = "degraded";
            message = "DLQ has elevated number of failed events";
            httpStatus = HttpStatus.OK;
            logger.error("DLQ health: DEGRADED - {} failed events", failedCount);
        } else {
            status = "healthy";
            message = "DLQ operating normally";
            httpStatus = HttpStatus.OK;
            logger.debug("DLQ health: HEALTHY - {} failed events", failedCount);
        }

        health.put("status", status);
        health.put("message", message);

        return ResponseEntity.status(httpStatus).body(health);
    }

    @PostMapping("/events/{eventId}/retry")
    public ResponseEntity<Map<String, Object>> forceRetry(@PathVariable Long eventId) {
        logger.info("Admin forcing retry for event: id={}", eventId);

        boolean success = dlqService.forceRetry(eventId);

        Map<String, Object> response = new HashMap<>();
        response.put("eventId", eventId);

        if (success) {
            response.put("message", "Event reset for retry");
            response.put("newStatus", DLQStatus.PENDING);

            logger.info("Event {} reset for retry by admin", eventId);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Event not found or cannot be retried");

            logger.warn("Failed to reset event {} for retry", eventId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/retry-all-failed")
    public ResponseEntity<Map<String, Object>> retryAllFailed() {
        logger.warn("Admin forcing retry for ALL failed events");

        int count = dlqService.retryAllFailed();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All failed events reset for retry");
        response.put("count", count);

        logger.info("Reset {} failed events for retry by admin", count);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/process-now")
    public ResponseEntity<Map<String, Object>> processNow() {
        logger.info("Admin triggering immediate DLQ processing");

        dlqService.processDeadLetterQueueAsync();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "DLQ processing triggered");
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.accepted().body(response);
    }
}
