package gov.va.evidence.events;

import java.time.Instant;

public record EvidenceSubmittedEvent(
        String veteranId,
        String claimId,
        String documentType,
        Instant createdDate
) {}

