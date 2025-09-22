package gov.va.evidence.messaging;

import gov.va.evidence.dto.EvidenceDTO;
import gov.va.evidence.events.EvidenceSubmittedEvent;
import gov.va.evidence.service.EvidenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.CompletableFuture;

import java.util.Base64;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
@ActiveProfiles("test")
class EvidenceKafkaIntegrationTest {

    @Autowired
    private EvidenceService evidenceService;

    @MockBean
    private KafkaTemplate<String, EvidenceSubmittedEvent> kafkaTemplate;

    @MockBean
    private EvidenceEventConsumer consumer; // disable actual @KafkaListener during tests

    @Test
    void publishesEventOnSubmit() {
        // Arrange
        EvidenceDTO dto = new EvidenceDTO();
        dto.setVeteranId("987654321");
        dto.setClaimId("CLAIM-1");
        dto.setDocumentType("PDF");
        dto.setBase64Document(Base64.getEncoder().encodeToString("test".getBytes()));

        when(kafkaTemplate.send(anyString(), anyString(), any(EvidenceSubmittedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        evidenceService.submitEvidence(dto);

        // Assert
        verify(kafkaTemplate, times(1)).send(anyString(), eq("CLAIM-1"), any(EvidenceSubmittedEvent.class));
    }
}
