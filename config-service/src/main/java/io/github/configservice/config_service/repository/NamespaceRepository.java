package io.github.configservice.config_service.repository;

import io.github.configservice.config_service.model.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NamespaceRepository extends JpaRepository<Namespace, UUID> {
    Optional<Namespace> findByKey(String key);
}
