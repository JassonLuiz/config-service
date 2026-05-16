package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.ConfigHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigHistoryRepository extends JpaRepository<ConfigHistory, Long> {

    Page<ConfigHistory> findByNamespaceAndEnvironmentAndConfigKey(
            String namespace,
            String environment,
            String configKey,
            Pageable pageable
    );

    Optional<ConfigHistory> findFirstByNamespaceAndEnvironmentAndConfigKeyOrderByChangedAtDesc(
            String namespace,
            String environment,
            String configKey
    );

    Page<ConfigHistory> findByNamespace(String namespace, Pageable pageable);

    Page<ConfigHistory> findByNamespaceAndEnvironment(
            String namespace,
            String environment,
            Pageable pageable
    );

    Page<ConfigHistory> findByChangedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    List<ConfigHistory> findByNamespaceAndEnvironmentAndConfigKeyAndChangedAtBetween(
            String namespace,
            String environment,
            String configKey,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<ConfigHistory> findByOperation(String operation, Pageable pageable);

    List<ConfigHistory> findByNamespaceAndEnvironmentAndConfigKeyAndOperation(
            String namespace,
            String environment,
            String configKey,
            String operation
    );

    Page<ConfigHistory> findByChangedBy(String changedBy, Pageable pageable);

    List<ConfigHistory> findByCorrelationId(String correlationId);

    long countByNamespaceAndEnvironmentAndConfigKey(
            String namespace,
            String environment,
            String configKey
    );

    long countByChangedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByOperation(String operation);

    @Query("SELECT h.namespace, h.environment, h.configKey, COUNT(h) " +
            "FROM ConfigHistory h " +
            "GROUP BY h.namespace, h.environment, h.configKey " +
            "ORDER BY COUNT(h) DESC")
    List<Object[]> findMostModifiedConfigs(Pageable pageable);

    @Query("SELECT h FROM ConfigHistory h " +
            "WHERE h.changedAt >= :since " +
            "ORDER BY h.changedAt DESC")
    List<ConfigHistory> findRecentChanges(@Param("since") LocalDateTime since);

    @Query("SELECT h FROM ConfigHistory h " +
            "WHERE h.namespace = :namespace " +
            "AND h.environment = :environment " +
            "AND h.operation = 'DELETE' " +
            "ORDER BY h.changedAt DESC")
    List<ConfigHistory> findDeletedConfigs(
            @Param("namespace") String namespace,
            @Param("environment") String environment
    );

    @Query("SELECT h FROM ConfigHistory h " +
            "WHERE h.namespace = :namespace " +
            "AND h.changedAt BETWEEN :start AND :end " +
            "ORDER BY h.changedAt DESC")
    Page<ConfigHistory> findNamespaceChangesInPeriod(
            @Param("namespace") String namespace,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
