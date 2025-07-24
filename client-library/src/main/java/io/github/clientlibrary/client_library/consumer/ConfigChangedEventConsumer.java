package io.github.clientlibrary.client_library.consumer;

import io.github.clientlibrary.client_library.event.ConfigChangedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigChangedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ConfigChangedEventConsumer.class);

//    @KafkaListener(
//            topics = "#{'${config.kafka.topics}'.split(',')}",
//            groupId = "${spring.application.name}"
//    )
    public void listen(ConsumerRecord<String, ConfigChangedEvent> record) {
        ConfigChangedEvent event = record.value();
        //log.info("✅ Kafka message received: {}", event);
    }
}
