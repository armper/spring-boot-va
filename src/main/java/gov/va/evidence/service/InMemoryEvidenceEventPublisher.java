package gov.va.evidence.service;

import gov.va.evidence.entity.Evidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEvidenceEventPublisher implements EvidenceEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(InMemoryEvidenceEventPublisher.class);

    @Override
    public void publishEvidenceSubmitted(Evidence evidence) {
        // Placeholder for Kafka/RabbitMQ integration
        log.info("Event: EvidenceSubmitted id={} claimId={} docType={}", evidence.getId(), evidence.getClaimId(), evidence.getDocumentType());
    }
}

