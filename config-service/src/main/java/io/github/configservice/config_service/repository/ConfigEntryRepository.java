package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.ConfigEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfigEntryRepository extends JpaRepository<ConfigEntry, UUID> {
    Optional<ConfigEntry> findByKeyAndEnvironment_KeyAndEnvironment_Namespace_Key(String Key, String envKey, String namespaceKey);
    List<ConfigEntry> findAllByEnvironment_KeyAndEnvironment_Namespace_Key(String envKey, String namespaceKey);
    boolean existsByEnvironment_KeyAndEnvironment_Namespace_Key(String envKey, String namespaceKey);
}
