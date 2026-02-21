package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto.*;

public interface CriminalCaseService {

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /** Set or update accused and court details. Creates on first call. */
    CriminalCaseDetailResponse setCaseDetails(Long caseId, CriminalCaseDetailRequest request);

    /** Get the full criminal case summary. */
    CriminalCaseDetailResponse getCaseDetail(Long caseId);

    // ─── Feature 1: Charge Management ────────────────────────────────────────────

    /** Add a new charge to the case. */
    CriminalChargeResponse addCharge(Long caseId, CriminalChargeRequest request);

    /** Update an existing charge (e.g. update plea, status, or outcome details). */
    CriminalChargeResponse updateCharge(Long caseId, Long chargeId, CriminalChargeRequest request);

    /** Remove a charge entered in error. */
    void deleteCharge(Long caseId, Long chargeId);

    // ─── Feature 2: Hearing History
    // ───────────────────────────────────────────────

    /** Record a court hearing/proceeding. */
    CourtHearingResponse addHearing(Long caseId, CourtHearingRequest request);

    /**
     * Update a hearing record (e.g. to correct the outcome or proceedings summary).
     */
    CourtHearingResponse updateHearing(Long caseId, Long hearingId, CourtHearingRequest request);

    /** Remove a hearing record entered in error. */
    void deleteHearing(Long caseId, Long hearingId);
}
