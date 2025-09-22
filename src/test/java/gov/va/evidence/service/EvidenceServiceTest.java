package gov.va.evidence.service;

import gov.va.evidence.dto.EvidenceDTO;
import gov.va.evidence.dto.EvidenceResponseDTO;
import gov.va.evidence.entity.Evidence;
import gov.va.evidence.exception.ValidationException;
import gov.va.evidence.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EvidenceServiceTest {

    private EvidenceRepository repository;
    private EvidenceEventPublisher publisher;
    private EvidenceService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(EvidenceRepository.class);
        publisher = Mockito.mock(EvidenceEventPublisher.class);
        service = new EvidenceService(repository, publisher);
    }

    @Test
    void submitEvidence_success() {
        EvidenceDTO dto = new EvidenceDTO();
        dto.setVeteranId("123456789");
        dto.setClaimId("CLAIM-1");
        dto.setDocumentType("PDF");
        dto.setBase64Document(Base64.getEncoder().encodeToString("hello".getBytes()));

        when(repository.save(any(Evidence.class))).thenAnswer(invocation -> {
            Evidence e = invocation.getArgument(0);
            e.setId(1L);
            e.setCreatedDate(Instant.now());
            return e;
        });

        EvidenceResponseDTO response = service.submitEvidence(dto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123456789", response.getVeteranId());
        assertEquals("CLAIM-1", response.getClaimId());
        assertEquals("PDF", response.getDocumentType());
        assertNotNull(response.getCreatedDate());

        verify(repository, times(1)).save(any(Evidence.class));
        verify(publisher, times(1)).publishEvidenceSubmitted(any(Evidence.class));
    }

    @Test
    void submitEvidence_invalidBase64_throws() {
        EvidenceDTO dto = new EvidenceDTO();
        dto.setVeteranId("123");
        dto.setClaimId("C1");
        dto.setDocumentType("PDF");
        dto.setBase64Document("not-base64");

        assertThrows(IllegalArgumentException.class, () -> service.submitEvidence(dto));
        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvidenceSubmitted(any());
    }

    @Test
    void submitEvidence_missingFields_throws() {
        EvidenceDTO dto = new EvidenceDTO();
        dto.setVeteranId(null);
        dto.setClaimId(" ");
        dto.setDocumentType("PDF");
        dto.setBase64Document(" ");

        assertThrows(ValidationException.class, () -> service.submitEvidence(dto));
        verify(repository, never()).save(any());
    }
}

