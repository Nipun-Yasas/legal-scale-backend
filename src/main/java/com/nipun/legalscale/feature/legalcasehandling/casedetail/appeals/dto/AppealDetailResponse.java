package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete Appeals case summary, covering all three features.
 */
@Data
@Builder
public class AppealDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    // ─── Feature 1: Original Case Linking ────────────────────────────────────────

    /** ID of the linked original case (null if external) */
    private Long originalCaseId;
    /** Title of the linked original case (null if external) */
    private String originalCaseTitle;
    /** Reference number of the linked original case (null if external) */
    private String originalCaseReferenceNumber;
    /** Free-text reference when original case is external */
    private String originalCaseReference;

    private String appealCourt;
    private LocalDate filingDate;
    private String groundsOfAppeal;
    private String notes;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Feature 2: Deadlines
    // ─────────────────────────────────────────────────────

    private List<AppealDeadlineResponse> deadlines;

    /** Count of deadlines currently marked PENDING and past their date */
    private long overdueDeadlineCount;

    // ─── Feature 3: Outcome ──────────────────────────────────────────────────────

    /** Null until a judgment is recorded */
    private AppealOutcomeResponse outcome;
}
