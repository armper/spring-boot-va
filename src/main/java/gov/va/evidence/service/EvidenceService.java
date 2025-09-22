package gov.va.evidence.service;

import gov.va.evidence.dto.EvidenceDTO;
import gov.va.evidence.dto.EvidenceResponseDTO;
import gov.va.evidence.entity.Evidence;
import gov.va.evidence.exception.ValidationException;
import gov.va.evidence.repository.EvidenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class EvidenceService {
    private static final Logger log = LoggerFactory.getLogger(EvidenceService.class);

    private final EvidenceRepository repository;
    private final EvidenceEventPublisher eventPublisher;

    public EvidenceService(EvidenceRepository repository, EvidenceEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EvidenceResponseDTO submitEvidence(EvidenceDTO dto) {
        validate(dto);

        // Document bytes would go to object storage (S3-like). Demo: just decode to validate.
        byte[] bytes = Base64.getDecoder().decode(dto.getBase64Document().getBytes(StandardCharsets.UTF_8));
        if (bytes.length == 0) {
            throw new ValidationException("Document content is empty after decoding");
        }

        Evidence entity = EvidenceMapper.toEntity(dto);
        entity.setCreatedDate(Instant.now());
        Evidence saved = repository.save(entity);

        // Publish event (strangler/event-driven ready)
        eventPublisher.publishEvidenceSubmitted(saved);

        return EvidenceMapper.toResponse(saved);
    }

    private void validate(EvidenceDTO dto) {
        if (dto == null) throw new ValidationException("Request body is required");
        if (isBlank(dto.getVeteranId())) throw new ValidationException("veteranId is required");
        if (isBlank(dto.getClaimId())) throw new ValidationException("claimId is required");
        if (isBlank(dto.getDocumentType())) throw new ValidationException("documentType is required");
        if (isBlank(dto.getBase64Document())) throw new ValidationException("base64Document is required");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

