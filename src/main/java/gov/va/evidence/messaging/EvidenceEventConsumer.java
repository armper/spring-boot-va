package gov.va.evidence.messaging;

import gov.va.evidence.events.EvidenceSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EvidenceEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(EvidenceEventConsumer.class);

    @KafkaListener(
            topics = "#{'${app.kafka.topics.evidence-submitted:evidence-submitted-topic}'}",
            containerFactory = "evidenceEventKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:evidence-service}"
    )
    public void onEvidenceSubmitted(EvidenceSubmittedEvent event) {
        // Simulate downstream processing
        log.info("Consumed EvidenceSubmittedEvent claimId={} veteranIdMasked={} type={} created={}"
                , event.claimId(), mask(event.veteranId()), event.documentType(), event.createdDate());
    }

    private String mask(String input) {
        if (input == null) return "N/A";
        String s = input.trim();
        if (s.length() <= 2) return s;
        return "*".repeat(s.length() - 2) + s.substring(s.length() - 2);
    }
}

