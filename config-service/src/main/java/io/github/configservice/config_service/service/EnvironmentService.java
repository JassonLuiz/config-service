package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.responseDTO.EnvironmentResponseDTO;
import io.github.configservice.config_service.dto.createDTO.EnvironmentCreateDTO;
import io.github.configservice.config_service.model.Environment;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import io.github.configservice.config_service.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

    private final EnvironmentRepository environmentRepo;
    private final NamespaceRepository namespaceRepo;

    public EnvironmentService(EnvironmentRepository environmentRepo, NamespaceRepository namespaceRepo) {
        this.environmentRepo = environmentRepo;
        this.namespaceRepo = namespaceRepo;
    }

    public List<EnvironmentResponseDTO> findAllByNamespace(String namespaceKey) {
        return environmentRepo.findAllByNamespace_Key(namespaceKey)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<EnvironmentResponseDTO> findByKeyAndNamespace(String envKey, String namespaceKey) {
        return environmentRepo.findByKeyAndNamespace_Key(envKey, namespaceKey)
                .map(this::toResponseDTO);
    }

    public EnvironmentResponseDTO createEnvironment(EnvironmentCreateDTO dto, String namespace) {
        log.debug("Creating environment with key='{}' for namespace='{}'", dto.key(), namespace);

        //Criar exceção NamespaceNotFoundException
        Namespace namespaceEntity = namespaceRepo.findByKey(namespace)
                .orElseThrow(() -> new RuntimeException("Namespace with key=" + namespace + " not found."));

        if (environmentRepo.existsByKeyAndNamespace_Key(dto.key(), namespace).isPresent()) {
            log.info("Environment with key='{}' already exists. Reusing it.", dto.key());
            throw new RuntimeException("Environment with key=" + dto.key() + " already exists. Reusing it.");
        }

        Environment environment = new Environment();
        environment.setKey(dto.key());
        environment.setDescription(dto.description());
        environment.setNamespace(namespaceEntity);
        environment.setCreatedAt(LocalDateTime.now());

        Environment saved = environmentRepo.save(environment);
        log.info("Environment created with key='{}'", saved.getKey());

        return toResponseDTO(saved);
    }

    public Optional<EnvironmentResponseDTO> update(String namespaceKey, String envKey, EnvironmentCreateDTO dto) {
        log.info("Updating environment with key='{}' for namespace='{}'", envKey, namespaceKey);

        return environmentRepo.findByKeyAndNamespace_Key(envKey, namespaceKey)
                .map(existing -> {
                    existing.setKey(dto.key());
                    existing.setDescription(dto.description());
                    existing.setUpdatedAt(LocalDateTime.now());
                    Environment updated = environmentRepo.save(existing);
                    log.info("Environment updated with key='{}'", updated.getKey());
                    return toResponseDTO(updated);
                });
    }

    //Add metodo delete

    private EnvironmentResponseDTO toResponseDTO(Environment environment) {
        return new EnvironmentResponseDTO(
                environment.getKey(),
                environment.getDescription(),
                environment.getNamespace().getKey(),
                environment.getCreatedAt(),
                environment.getUpdatedAt()
        );
    }
}
