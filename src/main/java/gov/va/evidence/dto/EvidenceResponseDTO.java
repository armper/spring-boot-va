package gov.va.evidence.dto;

import java.time.Instant;

public class EvidenceResponseDTO {
    private Long id;
    private String veteranId;
    private String claimId;
    private String documentType;
    private Instant createdDate;

    public EvidenceResponseDTO() {}

    public EvidenceResponseDTO(Long id, String veteranId, String claimId, String documentType, Instant createdDate) {
        this.id = id;
        this.veteranId = veteranId;
        this.claimId = claimId;
        this.documentType = documentType;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVeteranId() {
        return veteranId;
    }

    public void setVeteranId(String veteranId) {
        this.veteranId = veteranId;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}

