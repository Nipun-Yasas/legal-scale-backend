package com.nipun.legalscale.feature.agreementapproval.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AgreementVersionResponse {
    private Long id;
    private Long agreementId;
    private Integer versionNumber;
    private Long documentId;
    private String documentName;
    private String documentUrl;
    private LocalDateTime uploadedAt;
    private Long uploadedById;
    private String uploadedByName;
    private String versionNotes;
}
