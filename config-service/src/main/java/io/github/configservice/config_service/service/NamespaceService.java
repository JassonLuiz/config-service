package io.github.configservice.config_service.service;

import io.github.configservice.config_service.dto.createDTO.NamespaceCreateDTO;
import io.github.configservice.config_service.dto.responseDTO.NamespaceResponseDTO;
import io.github.configservice.config_service.model.Namespace;
import io.github.configservice.config_service.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NamespaceService {

    private static final Logger log = LoggerFactory.getLogger(NamespaceService.class);

    private final NamespaceRepository namespaceRepo;

    public NamespaceService(NamespaceRepository namespaceRepo) {
        this.namespaceRepo = namespaceRepo;
    }

    public List<NamespaceResponseDTO> findAll() {
        return namespaceRepo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<NamespaceResponseDTO> findByKey(String key) {
        return namespaceRepo.findByKey(key)
                .map(this::toResponseDTO);
    }

    public NamespaceResponseDTO createNamespace(NamespaceCreateDTO dto) {
        log.info("Creating namespace with key='{}'", dto.key());

        //Criar exceção NamespaceAlreadyExistsException
        if (namespaceRepo.existsByKey(dto.key()).isPresent()){
            log.info("Namespace with key='{}' already exists. Reusing it.", dto.key());
            throw new RuntimeException("Namespace with key=" + dto.key() + " already exists. Reusing it.");
        }

        Namespace namespace = new Namespace();
        namespace.setKey(dto.key());
        namespace.setDescription(dto.description());

        Namespace saved = namespaceRepo.save(namespace);
        log.info("Namespace created with key='{}'", saved.getKey());

        return toResponseDTO(saved);
    }

    //add metodo update
    //add metodo delete

    public NamespaceResponseDTO toResponseDTO(Namespace namespace) {
        return new NamespaceResponseDTO(
                namespace.getKey(),
                namespace.getDescription(),
                namespace.getCreatedAt(),
                namespace.getUpdatedAt()
        );
    }
}
