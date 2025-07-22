package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    Optional<Environment> findByKeyAndNamespace_Key(String key, String namespaceKey);
    List<Environment> findByNamespace_Key(String namespaceKey);
}
