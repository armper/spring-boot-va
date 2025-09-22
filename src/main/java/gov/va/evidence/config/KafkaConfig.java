package gov.va.evidence.config;

import gov.va.evidence.events.EvidenceSubmittedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@org.springframework.context.annotation.Profile("!test")
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.evidence-submitted:evidence-submitted-topic}")
    private String evidenceSubmittedTopic;

    @Bean
    public ProducerFactory<String, EvidenceSubmittedEvent> evidenceEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Rely on type headers for deserialization downstream
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EvidenceSubmittedEvent> evidenceEventKafkaTemplate() {
        return new KafkaTemplate<>(evidenceEventProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, EvidenceSubmittedEvent> evidenceEventConsumerFactory() {
        JsonDeserializer<EvidenceSubmittedEvent> jsonDeserializer = new JsonDeserializer<>(EvidenceSubmittedEvent.class);
        jsonDeserializer.addTrustedPackages("gov.va.evidence.events");
        return new DefaultKafkaConsumerFactory<>(Map.of(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
        ), new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EvidenceSubmittedEvent> evidenceEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EvidenceSubmittedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(evidenceEventConsumerFactory());
        return factory;
    }

    @Bean
    public NewTopic evidenceSubmittedTopic() {
        // 1 partition, replication 1 for local development
        return new NewTopic(evidenceSubmittedTopic, 1, (short) 1);
    }
}
