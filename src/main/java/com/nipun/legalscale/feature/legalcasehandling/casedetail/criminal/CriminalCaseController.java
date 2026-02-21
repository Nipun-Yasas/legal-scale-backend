package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Criminal case detail endpoints covering:
 * Feature 1 – Charge Management (statute, section, offence details, plea,
 * outcome)
 * Feature 2 – Hearing History (court proceedings timeline)
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type CRIMINAL for all operations.
 *
 * Base path: /api/cases/{caseId}/criminal
 */
@RestController
@RequestMapping("/api/cases/{caseId}/criminal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class CriminalCaseController {

    private final CriminalCaseService criminalCaseService;

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/criminal/details
     * Set or update accused info and court details.
     * Creates the record on first call; updates on subsequent calls.
     */
    @PutMapping("/details")
    public ResponseEntity<CriminalCaseDetailResponse> setCaseDetails(
            @PathVariable Long caseId,
            @Valid @RequestBody CriminalCaseDetailRequest request) {
        return ResponseEntity.ok(criminalCaseService.setCaseDetails(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/criminal
     * Full summary: accused and court info, all charges with status counts,
     * full hearing history, and next scheduled hearing date.
     */
    @GetMapping
    public ResponseEntity<CriminalCaseDetailResponse> getCaseDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(criminalCaseService.getCaseDetail(caseId));
    }

    // ─── Feature 1: Charge Management ────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/criminal/charges
     * Add a new charge (statute, section, offence name, description, penalty).
     * plea defaults to NO_PLEA_ENTERED; status defaults to PENDING.
     */
    @PostMapping("/charges")
    public ResponseEntity<CriminalChargeResponse> addCharge(
            @PathVariable Long caseId,
            @Valid @RequestBody CriminalChargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(criminalCaseService.addCharge(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/criminal/charges/{chargeId}
     * Update an existing charge — typically to record the plea, update status,
     * or note the outcome (sentence or withdrawal reason).
     */
    @PutMapping("/charges/{chargeId}")
    public ResponseEntity<CriminalChargeResponse> updateCharge(
            @PathVariable Long caseId,
            @PathVariable Long chargeId,
            @Valid @RequestBody CriminalChargeRequest request) {
        return ResponseEntity.ok(criminalCaseService.updateCharge(caseId, chargeId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/criminal/charges/{chargeId}
     * Remove a charge entered in error.
     */
    @DeleteMapping("/charges/{chargeId}")
    public ResponseEntity<Void> deleteCharge(
            @PathVariable Long caseId,
            @PathVariable Long chargeId) {
        criminalCaseService.deleteCharge(caseId, chargeId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 2: Hearing History
    // ───────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/criminal/hearings
     * Record a court hearing/proceeding.
     * Rule: nextHearingDate is required when outcome is ADJOURNED.
     */
    @PostMapping("/hearings")
    public ResponseEntity<CourtHearingResponse> addHearing(
            @PathVariable Long caseId,
            @Valid @RequestBody CourtHearingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(criminalCaseService.addHearing(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/criminal/hearings/{hearingId}
     * Update a hearing record (e.g. correct outcome or proceedings summary).
     */
    @PutMapping("/hearings/{hearingId}")
    public ResponseEntity<CourtHearingResponse> updateHearing(
            @PathVariable Long caseId,
            @PathVariable Long hearingId,
            @Valid @RequestBody CourtHearingRequest request) {
        return ResponseEntity.ok(criminalCaseService.updateHearing(caseId, hearingId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/criminal/hearings/{hearingId}
     * Remove a hearing record entered in error.
     */
    @DeleteMapping("/hearings/{hearingId}")
    public ResponseEntity<Void> deleteHearing(
            @PathVariable Long caseId,
            @PathVariable Long hearingId) {
        criminalCaseService.deleteHearing(caseId, hearingId);
        return ResponseEntity.noContent().build();
    }
}
