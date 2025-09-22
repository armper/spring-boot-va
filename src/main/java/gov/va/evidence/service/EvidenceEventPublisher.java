package gov.va.evidence.service;

import gov.va.evidence.entity.Evidence;

public interface EvidenceEventPublisher {
    void publishEvidenceSubmitted(Evidence evidence);
}

