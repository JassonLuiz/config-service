package io.github.configservice.config_service.config;

import io.github.configservice.config_service.event.ConfigChangedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, ConfigChangedEvent> producerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConfigChangedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, ConfigChangedEvent> consumerFactory,
            KafkaTemplate<String, ConfigChangedEvent> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ConfigChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(2000L, 3)
        ));
        return factory;
    }
}
