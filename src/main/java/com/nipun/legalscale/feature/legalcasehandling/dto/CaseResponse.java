package com.nipun.legalscale.feature.legalcasehandling.dto;

import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CaseResponse {

    private Long id;
    private String caseTitle;
    private CaseType caseType;
    private String referenceNumber;
    private String partiesInvolved;
    private String natureOfCase;
    private LocalDate dateOfOccurrenceOrFiling;
    private String courtOrAuthority;
    private BigDecimal financialExposure;
    private String summaryOfFacts;
    private CaseStatus status;

    // Audit
    private String createdSupervisorName;
    private String createdSupervisorEmail;
    private LocalDateTime createdAt;

    private String assignedOfficerName;
    private String assignedOfficerEmail;

    private LocalDateTime assignedAt;

    private String approvedByName;
    private String approvedByEmail;
    private LocalDateTime approvedAt;

    private String closedByName;
    private String closedByEmail;
    private LocalDateTime closedAt;
    private String closingRemarks;

    private List<CaseCommentResponse> comments;

    private List<DocumentResponse> supportingAttachments;
}
