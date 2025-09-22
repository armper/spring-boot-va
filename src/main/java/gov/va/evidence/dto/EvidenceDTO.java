package gov.va.evidence.dto;

import jakarta.validation.constraints.NotBlank;

public class EvidenceDTO {
    @NotBlank
    private String veteranId;

    @NotBlank
    private String claimId;

    @NotBlank
    private String documentType;

    // Placeholder for demo; not persisted
    @NotBlank
    private String base64Document;

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

    public String getBase64Document() {
        return base64Document;
    }

    public void setBase64Document(String base64Document) {
        this.base64Document = base64Document;
    }
}

