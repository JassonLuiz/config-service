package io.github.springexample.springexample1.config;

import io.github.clientlibrary.client_library.event.ConfigEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${config.client.kafka.bootstrap-servers:${spring.kafka.bootstrap-servers:localhost:9092}}")
    private String bootstrapServers;

    @Value("${config.client.kafka.group-id:${spring.application.name:default-group}}")
    private String groupId;

    @Value("${config.client.kafka.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${config.client.kafka.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    @Value("${config.client.kafka.max-poll-records:500}")
    private int maxPollRecords;

    @Bean("configEventConsumerFactory")
    public ConsumerFactory<String, ConfigEvent> configEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);


        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "io.github.clientlibrary.client_library.event");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ConfigEvent.class.getName());

        String typeMappings =
                "configEvent:" + ConfigEvent.class.getName() + "," +
                        "io.github.clientlibrary.client_library.event.ConfigEvent:" + ConfigEvent.class.getName();

        props.put(JsonDeserializer.TYPE_MAPPINGS, typeMappings);

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(ConfigEvent.class));
    }

    @Bean("configEventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ConfigEvent> configEventListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConfigEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(configEventConsumerFactory());
        factory.setConcurrency(1);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.getContainerProperties().setIdleEventInterval(60000L);

        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());

        return factory;
    }
}
