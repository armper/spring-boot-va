package gov.va.evidence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evidence")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String veteranId;

    @Column(nullable = false, length = 64)
    private String claimId;

    @Column(nullable = false, length = 64)
    private String documentType;

    @Column(nullable = false, updatable = false)
    private Instant createdDate;

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

