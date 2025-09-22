package gov.va.evidence.api;

import gov.va.evidence.dto.EvidenceDTO;
import gov.va.evidence.dto.EvidenceResponseDTO;
import gov.va.evidence.service.EvidenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/evidence", produces = MediaType.APPLICATION_JSON_VALUE)
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvidenceResponseDTO> submit(@Valid @RequestBody EvidenceDTO dto) {
        EvidenceResponseDTO response = evidenceService.submitEvidence(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

