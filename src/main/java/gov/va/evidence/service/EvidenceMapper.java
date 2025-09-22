package gov.va.evidence.service;

import gov.va.evidence.dto.EvidenceDTO;
import gov.va.evidence.dto.EvidenceResponseDTO;
import gov.va.evidence.entity.Evidence;

class EvidenceMapper {

    static Evidence toEntity(EvidenceDTO dto) {
        Evidence e = new Evidence();
        e.setVeteranId(dto.getVeteranId());
        e.setClaimId(dto.getClaimId());
        e.setDocumentType(dto.getDocumentType());
        return e;
    }

    static EvidenceResponseDTO toResponse(Evidence e) {
        return new EvidenceResponseDTO(
                e.getId(),
                e.getVeteranId(),
                e.getClaimId(),
                e.getDocumentType(),
                e.getCreatedDate());
    }
}

