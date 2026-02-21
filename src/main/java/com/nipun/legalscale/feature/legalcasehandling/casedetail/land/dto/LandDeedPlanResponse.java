package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.LandDocumentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LandDeedPlanResponse {

    private Long id;
    private Long caseId;
    private LandDocumentType documentType;
    private String documentReference;
    private LocalDate issueDate;
    private String issuingAuthority;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;

    // ─── Uploaded document info (null if registry-only) ──────────────────────────
    private Long uploadedDocumentId;
    private String uploadedDocumentName;
    private String uploadedDocumentUrl;
}
