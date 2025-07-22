package io.github.configservice.config_service.service;

import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NamespaceService {

    private static final Logger log = LoggerFactory.getLogger(NamespaceService.class);

    private final NamespaceRepository namespaceRepo;

    public NamespaceService(NamespaceRepository namespaceRepo) {
        this.namespaceRepo = namespaceRepo;
    }

    public Namespace findOrCreate(String key, String description) {
        log.debug("Looking for existing namespace with key='{}'", key);

        Optional<Namespace> existing = namespaceRepo.findByKey(key);

        if (existing.isPresent()){
            log.info("Namespace with key='{}' already exists. Reusing it.", key);
            return existing.get();
        }

        log.info("No existing namespace found with key='{}'. Creating new one.", key);

        Namespace namespace = new Namespace();
        namespace.setKey(key);
        namespace.setDescription(description);
        return namespaceRepo.save(namespace);
    }
}
