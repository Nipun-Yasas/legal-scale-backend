package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.SettlementStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Damages Recovery case detail endpoints.
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type DAMAGES_RECOVERY for all operations.
 *
 * Base path: /api/cases/{caseId}/damages-recovery
 */
@RestController
@RequestMapping("/api/cases/{caseId}/damages-recovery")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class DamagesRecoveryController {

    private final DamagesRecoveryService damagesRecoveryService;

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/damages-recovery/claim
     * Set or update the total compensation claimed. Creates the record on first
     * call.
     */
    @PutMapping("/claim")
    public ResponseEntity<DamagesRecoveryDetailResponse> setCompensationClaimed(
            @PathVariable Long caseId,
            @Valid @RequestBody DamagesRecoveryDetailRequest request) {
        return ResponseEntity.ok(damagesRecoveryService.setCompensationClaimed(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/damages-recovery
     * Full summary: claim amount, assessed value, total received, outstanding
     * balance,
     * all assessments, all payments, and settlement agreement.
     */
    @GetMapping
    public ResponseEntity<DamagesRecoveryDetailResponse> getDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(damagesRecoveryService.getDetail(caseId));
    }

    // ─── Damage Assessments
    // ───────────────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/damages-recovery/assessments
     * Record a new damage assessment (category, description, estimated value,
     * assessor).
     */
    @PostMapping("/assessments")
    public ResponseEntity<DamageAssessmentResponse> addAssessment(
            @PathVariable Long caseId,
            @Valid @RequestBody DamageAssessmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(damagesRecoveryService.addAssessment(caseId, request));
    }

    /**
     * PATCH /api/cases/{caseId}/damages-recovery/assessments/{assessmentId}/status
     * Update the status of an assessment (PENDING → COMPLETED or DISPUTED).
     */
    @PatchMapping("/assessments/{assessmentId}/status")
    public ResponseEntity<DamageAssessmentResponse> updateAssessmentStatus(
            @PathVariable Long caseId,
            @PathVariable Long assessmentId,
            @Valid @RequestBody AssessmentStatusUpdateRequest request) {
        return ResponseEntity.ok(damagesRecoveryService.updateAssessmentStatus(caseId, assessmentId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/damages-recovery/assessments/{assessmentId}
     * Remove a damage assessment.
     */
    @DeleteMapping("/assessments/{assessmentId}")
    public ResponseEntity<Void> deleteAssessment(
            @PathVariable Long caseId,
            @PathVariable Long assessmentId) {
        damagesRecoveryService.deleteAssessment(caseId, assessmentId);
        return ResponseEntity.noContent().build();
    }

    // ─── Compensation Payments
    // ────────────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/damages-recovery/payments
     * Record a compensation payment received.
     */
    @PostMapping("/payments")
    public ResponseEntity<CompensationPaymentResponse> recordPayment(
            @PathVariable Long caseId,
            @Valid @RequestBody CompensationPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(damagesRecoveryService.recordPayment(caseId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/damages-recovery/payments/{paymentId}
     * Remove a mistakenly recorded payment.
     */
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long caseId,
            @PathVariable Long paymentId) {
        damagesRecoveryService.deletePayment(caseId, paymentId);
        return ResponseEntity.noContent().build();
    }

    // ─── Settlement Management
    // ────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/damages-recovery/settlement
     * Propose or update a settlement agreement (agreed amount, terms, date).
     * Resets status to PROPOSED on update.
     */
    @PutMapping("/settlement")
    public ResponseEntity<SettlementAgreementResponse> proposeSettlement(
            @PathVariable Long caseId,
            @Valid @RequestBody SettlementAgreementRequest request) {
        return ResponseEntity.ok(damagesRecoveryService.proposeSettlement(caseId, request));
    }

    /**
     * PATCH /api/cases/{caseId}/damages-recovery/settlement/status
     * Update settlement status: AGREED, REJECTED, or EXECUTED.
     * Expected body: { "status": "AGREED" }
     */
    @PatchMapping("/settlement/status")
    public ResponseEntity<SettlementAgreementResponse> updateSettlementStatus(
            @PathVariable Long caseId,
            @RequestParam SettlementStatus status) {
        return ResponseEntity.ok(damagesRecoveryService.updateSettlementStatus(caseId, status));
    }
}
