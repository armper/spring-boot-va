package gov.va.evidence.repository;

import gov.va.evidence.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
}

