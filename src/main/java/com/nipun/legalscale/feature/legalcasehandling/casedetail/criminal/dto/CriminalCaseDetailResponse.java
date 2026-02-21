package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete Criminal Case summary covering both features.
 */
@Data
@Builder
public class CriminalCaseDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    // ─── Accused & Court Info ────────────────────────────────────────────────────

    private String accusedName;
    private String accusedIdNumber;
    private String accusedAddress;
    private String court;
    private String courtCaseNumber;
    private String presidingJudge;
    private LocalDate courtFilingDate;
    private String notes;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Feature 1: Charges ──────────────────────────────────────────────────────

    private List<CriminalChargeResponse> charges;

    /** Quick summary counts */
    private long totalCharges;
    private long pendingCharges;
    private long convictedCharges;
    private long acquittedCharges;
    private long withdrawnCharges;

    // ─── Feature 2: Hearing History ──────────────────────────────────────────────

    /** Full proceedings history in chronological order */
    private List<CourtHearingResponse> hearings;

    /**
     * The next scheduled hearing date derived from the most recent ADJOURNED
     * hearing
     */
    private LocalDate nextHearingDate;
    private String nextHearingPurpose;
}
