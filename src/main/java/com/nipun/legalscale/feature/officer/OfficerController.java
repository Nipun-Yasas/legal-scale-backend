package com.nipun.legalscale.feature.officer;

import com.nipun.legalscale.feature.legalcasehandling.CaseService;
import com.nipun.legalscale.feature.legalcasehandling.dto.*;
import com.nipun.legalscale.feature.agreementapproval.dto.AgreementResponse;
import com.nipun.legalscale.feature.agreementapproval.dto.ReviewAgreementRequest;
import com.nipun.legalscale.feature.agreementapproval.service.AgreementService;
import com.nipun.legalscale.feature.agreementapproval.enums.AgreementStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Officer-only endpoints.
 * Role guard: LEGAL_OFFICER
 */
@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LEGAL_OFFICER')")
public class OfficerController {

    private final CaseService caseService;
    private final AgreementService agreementService;

    /**
     * GET /api/officer/cases
     * View all cases that have been assigned to the currently authenticated
     * officer.
     */
    @GetMapping("/cases")
    public ResponseEntity<List<CaseResponse>> getMyCases() {
        return ResponseEntity.ok(caseService.getCasesAssignedToCurrentOfficer());
    }

    /**
     * GET /api/officer/cases/{id}
     * View details of a specific case (must be assigned to this officer).
     */
    @GetMapping("/cases/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    /**
     * PATCH /api/officer/cases/{id}/status
     * Change the status of an assigned case.
     * When approving (ACTIVE), approvedBy / approvedAt are recorded.
     * When closing (CLOSED), closingRemarks are mandatory, closedBy / closedAt are
     * recorded.
     * Officers can only act on cases assigned to them.
     */
    @PatchMapping("/cases/{id}/status")
    public ResponseEntity<CaseResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request) {
        return ResponseEntity.ok(caseService.officerUpdateStatus(id, request));
    }

    /**
     * POST /api/officer/cases/{id}/comments
     * Add a comment to an assigned case.
     */
    @PostMapping("/cases/{id}/comments")
    public ResponseEntity<CaseCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(caseService.officerAddComment(id, request));
    }

    // agreements

    /**
     * POST /api/officer/agreements/{id}/respond-revision
     * Respond to a revision in an agreement.
     */

    @PostMapping("/agreements/{id}/pending-approval")
    public ResponseEntity<AgreementResponse> markReviewOk(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewAgreementRequest request) {
        if (request == null) {
            request = new ReviewAgreementRequest();
        }
        request.setReviewStatus(AgreementStatus.PENDING_APPROVAL);
        return ResponseEntity.ok(agreementService.reviewAgreement(id, request));
    }

    @PostMapping("/agreements/{id}/respond-revision")
    public ResponseEntity<AgreementResponse> respondToRevision(
            @PathVariable Long id,
            @RequestBody @Valid ReviewAgreementRequest request) {
        return ResponseEntity.ok(agreementService.respondToRevision(id, request));
    }

    @GetMapping("/agreements/pending")
    public ResponseEntity<List<AgreementResponse>> getAgreementsForReview() {
        return ResponseEntity.ok(agreementService.getAgreementsForReview());
    }

    @PostMapping("/agreements/{id}/review")
    public ResponseEntity<AgreementResponse> reviewAgreement(
            @PathVariable Long id,
            @RequestBody @Valid ReviewAgreementRequest request) {
        return ResponseEntity.ok(agreementService.reviewAgreement(id, request));
    }

}
