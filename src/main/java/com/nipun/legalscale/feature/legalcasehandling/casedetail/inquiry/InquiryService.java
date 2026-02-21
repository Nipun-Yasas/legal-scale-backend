package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto.*;

public interface InquiryService {

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /** Set or update inquiry mandate details. Creates on first call. */
    InquiryDetailResponse setInquiryDetails(Long caseId, InquiryDetailRequest request);

    /** Get the full inquiry summary. */
    InquiryDetailResponse getInquiryDetail(Long caseId);

    // ─── Feature 1: Panel Setup
    // ───────────────────────────────────────────────────

    /** Add a member to the inquiry panel. Enforces one CHAIRPERSON per panel. */
    PanelMemberResponse addPanelMember(Long caseId, PanelMemberRequest request);

    /** Remove a panel member. */
    void removePanelMember(Long caseId, Long memberId);

    // ─── Feature 2: Findings & Recommendations
    // ────────────────────────────────────

    /** Record a new finding (finding number is auto-assigned). */
    InquiryFindingResponse addFinding(Long caseId, InquiryFindingRequest request);

    /** Update a finding (e.g. to refine the recommendation). */
    InquiryFindingResponse updateFinding(Long caseId, Long findingId, InquiryFindingRequest request);

    /** Remove a finding entered in error. */
    void deleteFinding(Long caseId, Long findingId);

    // ─── Feature 3: Decision Tracking
    // ─────────────────────────────────────────────

    /** Record a management decision (optionally linked to a finding). */
    InquiryDecisionResponse addDecision(Long caseId, InquiryDecisionRequest request);

    /** Update the implementation status of a decision. */
    InquiryDecisionResponse updateDecisionStatus(Long caseId, Long decisionId, DecisionStatusUpdateRequest request);

    /** Remove a decision entered in error. */
    void deleteDecision(Long caseId, Long decisionId);
}
