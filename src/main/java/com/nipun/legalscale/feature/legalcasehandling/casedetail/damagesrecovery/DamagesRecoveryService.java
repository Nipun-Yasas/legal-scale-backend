package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.SettlementStatus;

public interface DamagesRecoveryService {

    // ─── Header ─────────────────────────────────────────────────────────────────

    /**
     * Set or update the total compensation claimed. Creates the detail on first
     * call.
     */
    DamagesRecoveryDetailResponse setCompensationClaimed(Long caseId, DamagesRecoveryDetailRequest request);

    /** Get the full damages recovery summary including all sub-feature data. */
    DamagesRecoveryDetailResponse getDetail(Long caseId);

    // ─── Damage Assessments ──────────────────────────────────────────────────────

    /** Record a new damage assessment (valuation). */
    DamageAssessmentResponse addAssessment(Long caseId, DamageAssessmentRequest request);

    /**
     * Update the status of an existing assessment (PENDING → COMPLETED / DISPUTED).
     */
    DamageAssessmentResponse updateAssessmentStatus(Long caseId, Long assessmentId,
            AssessmentStatusUpdateRequest request);

    /** Remove a damage assessment. */
    void deleteAssessment(Long caseId, Long assessmentId);

    // ─── Compensation Payments ───────────────────────────────────────────────────

    /** Record a compensation payment received. */
    CompensationPaymentResponse recordPayment(Long caseId, CompensationPaymentRequest request);

    /** Remove a mistakenly recorded payment. */
    void deletePayment(Long caseId, Long paymentId);

    // ─── Settlement Management ───────────────────────────────────────────────────

    /** Propose or update the settlement agreement for this case. */
    SettlementAgreementResponse proposeSettlement(Long caseId, SettlementAgreementRequest request);

    /** Update the status of the settlement (AGREED, REJECTED, EXECUTED). */
    SettlementAgreementResponse updateSettlementStatus(Long caseId, SettlementStatus newStatus);
}
