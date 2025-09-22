package gov.va.evidence.messaging;

import gov.va.evidence.entity.Evidence;
import gov.va.evidence.events.EvidenceSubmittedEvent;
import gov.va.evidence.service.EvidenceEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class KafkaEvidenceEventPublisher implements EvidenceEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaEvidenceEventPublisher.class);

    private final KafkaTemplate<String, EvidenceSubmittedEvent> kafkaTemplate;
    private final String topic;

    public KafkaEvidenceEventPublisher(
            KafkaTemplate<String, EvidenceSubmittedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.evidence-submitted:evidence-submitted-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishEvidenceSubmitted(Evidence evidence) {
        EvidenceSubmittedEvent event = new EvidenceSubmittedEvent(
                evidence.getVeteranId(),
                evidence.getClaimId(),
                evidence.getDocumentType(),
                evidence.getCreatedDate()
        );
        String key = evidence.getClaimId();
        java.util.concurrent.CompletableFuture<?> future = kafkaTemplate.send(topic, key, event);
        future.whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to publish EvidenceSubmittedEvent for claimId={}:", key, ex);
            } else {
                log.info("Published EvidenceSubmittedEvent to topic={} key={}", topic, key);
            }
        });
    }
}
