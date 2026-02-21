package com.nipun.legalscale.feature.legalcasehandling.casedetail.land;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto.*;

public interface LandCaseService {

    // ─── Feature 1: Land Reference Management ────────────────────────────────────

    /**
     * Set or update all land reference and survey details. Creates on first call.
     */
    LandDetailResponse setLandDetails(Long caseId, LandDetailRequest request);

    /** Get the full land case summary. */
    LandDetailResponse getLandDetail(Long caseId);

    // ─── Feature 2: Ownership History ────────────────────────────────────────────

    /** Add a new entry to the ownership history chain. */
    OwnershipRecordResponse addOwnershipRecord(Long caseId, OwnershipRecordRequest request);

    /**
     * Update an existing ownership record (e.g. to set the end date when ownership
     * transfers).
     */
    OwnershipRecordResponse updateOwnershipRecord(Long caseId, Long recordId, OwnershipRecordRequest request);

    /** Remove an ownership record entered in error. */
    void deleteOwnershipRecord(Long caseId, Long recordId);

    // ─── Feature 3: Deed & Plan Management ───────────────────────────────────────

    /** Register a deed or plan entry (with optional uploaded document link). */
    LandDeedPlanResponse addDeedPlan(Long caseId, LandDeedPlanRequest request);

    /** Remove a deed/plan entry. */
    void deleteDeedPlan(Long caseId, Long deedPlanId);
}
