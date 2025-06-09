package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigEntryRepository extends JpaRepository<ConfigEntry, Long> {
    Optional<ConfigEntry> findByNamespaceAndEnvironmentAndKeyName(String namespace, String environment, String keyName);
}
