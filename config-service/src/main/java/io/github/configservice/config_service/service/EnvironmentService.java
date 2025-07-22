package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

    private final EnvironmentRepository environmentRepo;

    public EnvironmentService(EnvironmentRepository environmentRepo) {
        this.environmentRepo = environmentRepo;
    }

    public Environment findOrCreate(String key, String description, Namespace namespace) {
        log.debug("Searching for environment with key '{}' in namespace '{}'", key, namespace.getKey());

        Optional<Environment> existing = environmentRepo.findByKeyAndNamespace_Key(key, namespace.getKey());

        if (existing.isPresent()) {
            log.info("Reusing existing environment: key='{}', namespace='{}'", key, namespace.getKey());
            return existing.get();
        }

        log.info("Creating new environment: key='{}', namespace='{}'", key, namespace.getKey());

        Environment environment = new Environment();
        environment.setKey(key);
        environment.setDescription(description);
        environment.setNamespace(namespace);
        return environmentRepo.save(environment);
    }
}
