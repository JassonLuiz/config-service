package io.github.configservice.config_service.service;

import io.github.configservice.config_service.event.EventType;
import io.github.configservice.config_service.repository.ConfigEntryRepository;
import io.github.configservice.config_service.repository.EnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final ConfigEntryRepository configRepo;
    private final KafkaProducerService kafkaProducerService;


    public SyncService(ConfigEntryRepository configRepo, KafkaProducerService kafkaProducerService) {
        this.configRepo = configRepo;
        this.kafkaProducerService = kafkaProducerService;
    }

    public boolean forceSync(String namespace, String env) {
        boolean environmentExists = configRepo.existsByEnvironment_KeyAndEnvironment_Namespace_Key(env, namespace);

        if (!environmentExists) {
            log.warn("Sync ignored - environment not found: namespace='{}', env='{}'", namespace, env);
            return false;
        }

        log.info("Triggering sync-force for namespace='{}', env='{}'", namespace, env);
        kafkaProducerService.publishEvent(EventType.SYNC ,namespace, env, null);
        return true;
    }
}
