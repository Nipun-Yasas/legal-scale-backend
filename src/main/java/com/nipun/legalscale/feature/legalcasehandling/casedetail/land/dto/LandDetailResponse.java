package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete Land case summary covering all three features.
 */
@Data
@Builder
public class LandDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    // ─── Feature 1: Land Reference ───────────────────────────────────────────────

    private String landReferenceNumber;
    private String surveyPlanNumber;
    private String lotNumber;
    private String planNumber;
    private BigDecimal extent;
    private String extentUnit;
    private String province;
    private String district;
    private String dsDivision;
    private String gnDivision;
    private String address;
    private String landRegistryDivision;
    private String notes;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Feature 2: Ownership History ────────────────────────────────────────────

    /** Full ownership chain ordered by start date (earliest first) */
    private List<OwnershipRecordResponse> ownershipHistory;

    /** Convenience pointer to the current owner (ownershipEndDate == null) */
    private OwnershipRecordResponse currentOwner;

    // ─── Feature 3: Deeds & Plans ────────────────────────────────────────────────

    /** All registered deeds and plans (most recent first) */
    private List<LandDeedPlanResponse> deedsAndPlans;
}
