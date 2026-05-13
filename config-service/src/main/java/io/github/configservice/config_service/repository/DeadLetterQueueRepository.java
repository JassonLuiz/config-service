package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.DeadLetterMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeadLetterQueueRepository extends JpaRepository<DeadLetterMessage, Long> {

    @Query("SELECT dlq FROM DeadLetterMessage dlq " +
            "WHERE dlq.status = 'PENDING' " +
            "AND dlq.retryCount < :maxRetries " +
            "ORDER BY dlq.failedAt ASC")
    List<DeadLetterMessage> findPendingForRetry(@Param("maxRetries") int maxRetries);

    @Query("SELECT dlq FROM DeadLetterMessage dlq " +
            "WHERE dlq.status = 'PENDING' " +
            "AND dlq.retryCount < :maxRetries " +
            "ORDER BY dlq.failedAt ASC")
    Page<DeadLetterMessage> findPendingForRetryPaginated(
            @Param("maxRetries") int maxRetries,
            Pageable pageable);

    List<DeadLetterMessage> findByNamespace(String namespace);

    List<DeadLetterMessage> findByNamespaceAndEnvironment(String namespace, String environment);

    Page<DeadLetterMessage> findByStatus(String status, Pageable pageable);

    List<DeadLetterMessage> findByFailedAtAfter(LocalDateTime after);

    List<DeadLetterMessage> findByFailedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(String status);

    long countByNamespace(String namespace);

    long countByNamespaceAndStatus(String namespace, String status);

    @Modifying
    @Query("DELETE FROM DeadLetterMessage dlq " +
            "WHERE dlq.status = :status " +
            "AND dlq.failedAt < :cutoffDate")
    int deleteByStatusAndFailedAtBefore(
            @Param("status") String status,
            @Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM DeadLetterMessage dlq " +
            "WHERE dlq.status = 'FAILED' " +
            "AND dlq.failedAt < :cutoffDate")
    int deleteFailedOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    List<DeadLetterMessage> findByCorrelationId(String correlationId);

    List<DeadLetterMessage> findByConfigCountGreaterThan(int count);

    @Modifying
    @Query("UPDATE DeadLetterMessage dlq " +
            "SET dlq.status = 'PENDING' " +
            "WHERE dlq.status = 'RETRYING' " +
            "AND dlq.lastRetryAt < :cutoffTime")
    int resetStuckRetrying(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT DISTINCT dlq.targetTopic FROM DeadLetterMessage dlq " +
            "WHERE dlq.status = :status")
    List<String> findDistinctTopicsByStatus(@Param("status") String status);
}
