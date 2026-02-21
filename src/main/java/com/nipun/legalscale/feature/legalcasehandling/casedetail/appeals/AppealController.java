package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Appeals case detail endpoints.
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type APPEALS for all operations.
 *
 * Base path: /api/cases/{caseId}/appeals
 */
@RestController
@RequestMapping("/api/cases/{caseId}/appeals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class AppealController {

    private final AppealService appealService;

    // ─── Feature 1 + Header ──────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/appeals/details
     * Set or update the appeal header:
     * - Link to original case (internal by ID or external by reference string)
     * - Appeal court, filing date, grounds of appeal
     * Creates the record on first call; updates on subsequent calls.
     */
    @PutMapping("/details")
    public ResponseEntity<AppealDetailResponse> setAppealDetails(
            @PathVariable Long caseId,
            @Valid @RequestBody AppealDetailRequest request) {
        return ResponseEntity.ok(appealService.setAppealDetails(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/appeals
     * Full appeal summary: original case link, all deadlines (with overdue flag),
     * and the outcome record.
     */
    @GetMapping
    public ResponseEntity<AppealDetailResponse> getDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(appealService.getAppealDetail(caseId));
    }

    // ─── Feature 2: Deadline Tracking ────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/appeals/deadlines
     * Add a new deadline (FILING, HEARING, RESPONSE, DOCUMENT_SUBMISSION, etc.).
     */
    @PostMapping("/deadlines")
    public ResponseEntity<AppealDeadlineResponse> addDeadline(
            @PathVariable Long caseId,
            @Valid @RequestBody AppealDeadlineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appealService.addDeadline(caseId, request));
    }

    /**
     * PATCH /api/cases/{caseId}/appeals/deadlines/{deadlineId}/status
     * Update the status of a deadline.
     * - For EXTENDED: must also provide extendedDeadlineDate
     */
    @PatchMapping("/deadlines/{deadlineId}/status")
    public ResponseEntity<AppealDeadlineResponse> updateDeadlineStatus(
            @PathVariable Long caseId,
            @PathVariable Long deadlineId,
            @Valid @RequestBody DeadlineStatusUpdateRequest request) {
        return ResponseEntity.ok(appealService.updateDeadlineStatus(caseId, deadlineId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/appeals/deadlines/{deadlineId}
     * Remove a deadline.
     */
    @DeleteMapping("/deadlines/{deadlineId}")
    public ResponseEntity<Void> deleteDeadline(
            @PathVariable Long caseId,
            @PathVariable Long deadlineId) {
        appealService.deleteDeadline(caseId, deadlineId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 3: Outcome Recording ────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/appeals/outcome
     * Record or update the appeal judgment / outcome.
     * Creates on first call; updates on subsequent calls.
     */
    @PutMapping("/outcome")
    public ResponseEntity<AppealOutcomeResponse> recordOutcome(
            @PathVariable Long caseId,
            @Valid @RequestBody AppealOutcomeRequest request) {
        return ResponseEntity.ok(appealService.recordOutcome(caseId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/appeals/outcome
     * Remove an outcome entered in error.
     */
    @DeleteMapping("/outcome")
    public ResponseEntity<Void> deleteOutcome(@PathVariable Long caseId) {
        appealService.deleteOutcome(caseId);
        return ResponseEntity.noContent().build();
    }
}
