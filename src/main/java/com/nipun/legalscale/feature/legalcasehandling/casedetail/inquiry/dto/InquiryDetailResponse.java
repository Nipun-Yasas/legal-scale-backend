package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete Inquiry case summary covering all three features.
 */
@Data
@Builder
public class InquiryDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    // ─── Inquiry Mandate ─────────────────────────────────────────────────────────

    private String inquirySubject;
    private String commissionedBy;
    private LocalDate commissionedDate;
    private String termsOfReference;
    private LocalDate reportingDeadline;
    private LocalDate reportSubmittedDate;
    private boolean reportSubmitted;
    private boolean reportOverdue;
    private String notes;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Feature 1: Panel ────────────────────────────────────────────────────────

    private List<PanelMemberResponse> panelMembers;
    private int panelSize;

    // ─── Feature 2: Findings ─────────────────────────────────────────────────────

    private List<InquiryFindingResponse> findings;
    private long totalFindings;
    private long criticalFindings;
    private long majorFindings;
    private long minorFindings;

    // ─── Feature 3: Decisions ────────────────────────────────────────────────────

    private List<InquiryDecisionResponse> decisions;
    private long totalDecisions;
    private long pendingDecisions;
    private long inProgressDecisions;
    private long implementedDecisions;
    private long overdueDecisions;
}
