package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Inquiry case detail endpoints covering:
 * Feature 1 – Panel Setup (members, roles, chairperson enforcement)
 * Feature 2 – Findings & Recommendations (severity, auto-numbering,
 * recommendations)
 * Feature 3 – Decision Tracking (management decisions, status lifecycle,
 * overdue alerts)
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type INQUIRIES for all operations.
 *
 * Base path: /api/cases/{caseId}/inquiry
 */
@RestController
@RequestMapping("/api/cases/{caseId}/inquiry")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class InquiryController {

    private final InquiryService inquiryService;

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/inquiry/details
     * Set or update the inquiry mandate: subject, commissioned by,
     * terms of reference, reporting deadline, report submission date.
     */
    @PutMapping("/details")
    public ResponseEntity<InquiryDetailResponse> setInquiryDetails(
            @PathVariable Long caseId,
            @Valid @RequestBody InquiryDetailRequest request) {
        return ResponseEntity.ok(inquiryService.setInquiryDetails(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/inquiry
     * Full summary: mandate (with reportSubmitted and reportOverdue flags),
     * panel members, findings with severity counts, decisions with status counts
     * and overdue count.
     */
    @GetMapping
    public ResponseEntity<InquiryDetailResponse> getDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(inquiryService.getInquiryDetail(caseId));
    }

    // ─── Feature 1: Panel Setup
    // ───────────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/inquiry/panel
     * Add a member to the inquiry panel.
     * Rule: Only one CHAIRPERSON is allowed per panel.
     */
    @PostMapping("/panel")
    public ResponseEntity<PanelMemberResponse> addPanelMember(
            @PathVariable Long caseId,
            @Valid @RequestBody PanelMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inquiryService.addPanelMember(caseId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/inquiry/panel/{memberId}
     * Remove a panel member.
     */
    @DeleteMapping("/panel/{memberId}")
    public ResponseEntity<Void> removePanelMember(
            @PathVariable Long caseId,
            @PathVariable Long memberId) {
        inquiryService.removePanelMember(caseId, memberId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 2: Findings & Recommendations
    // ────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/inquiry/findings
     * Record a new inquiry finding.
     * Finding number is auto-assigned (incremented from last finding).
     */
    @PostMapping("/findings")
    public ResponseEntity<InquiryFindingResponse> addFinding(
            @PathVariable Long caseId,
            @Valid @RequestBody InquiryFindingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inquiryService.addFinding(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/inquiry/findings/{findingId}
     * Update a finding (e.g. refine description or recommendation).
     */
    @PutMapping("/findings/{findingId}")
    public ResponseEntity<InquiryFindingResponse> updateFinding(
            @PathVariable Long caseId,
            @PathVariable Long findingId,
            @Valid @RequestBody InquiryFindingRequest request) {
        return ResponseEntity.ok(inquiryService.updateFinding(caseId, findingId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/inquiry/findings/{findingId}
     * Remove a finding entered in error.
     */
    @DeleteMapping("/findings/{findingId}")
    public ResponseEntity<Void> deleteFinding(
            @PathVariable Long caseId,
            @PathVariable Long findingId) {
        inquiryService.deleteFinding(caseId, findingId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 3: Decision Tracking ────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/inquiry/decisions
     * Record a management decision.
     * Optionally link to a specific finding via relatedFindingId.
     */
    @PostMapping("/decisions")
    public ResponseEntity<InquiryDecisionResponse> addDecision(
            @PathVariable Long caseId,
            @Valid @RequestBody InquiryDecisionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inquiryService.addDecision(caseId, request));
    }

    /**
     * PATCH /api/cases/{caseId}/inquiry/decisions/{decisionId}/status
     * Update the implementation status of a decision.
     * Rule: IMPLEMENTED status requires an implementedDate.
     */
    @PatchMapping("/decisions/{decisionId}/status")
    public ResponseEntity<InquiryDecisionResponse> updateDecisionStatus(
            @PathVariable Long caseId,
            @PathVariable Long decisionId,
            @Valid @RequestBody DecisionStatusUpdateRequest request) {
        return ResponseEntity.ok(inquiryService.updateDecisionStatus(caseId, decisionId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/inquiry/decisions/{decisionId}
     * Remove a decision entered in error.
     */
    @DeleteMapping("/decisions/{decisionId}")
    public ResponseEntity<Void> deleteDecision(
            @PathVariable Long caseId,
            @PathVariable Long decisionId) {
        inquiryService.deleteDecision(caseId, decisionId);
        return ResponseEntity.noContent().build();
    }
}
