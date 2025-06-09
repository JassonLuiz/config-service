package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.ConfigEntryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigEntryHistoryRepository extends JpaRepository<ConfigEntryHistory, Long> {
    List<ConfigEntryHistory> findByConfigEntryId(Long configEntryId);
}
