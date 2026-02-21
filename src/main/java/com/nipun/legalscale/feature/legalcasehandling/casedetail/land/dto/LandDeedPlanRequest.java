package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.LandDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LandDeedPlanRequest {

    @NotNull(message = "Document type is required")
    private LandDocumentType documentType;

    @NotBlank(message = "Document reference number is required")
    private String documentReference;

    private LocalDate issueDate;
    private String issuingAuthority;

    /**
     * ID of an already-uploaded Document (from POST /api/documents/upload).
     * Leave null to record registry entry only.
     */
    private Long uploadedDocumentId;

    private String notes;
}
