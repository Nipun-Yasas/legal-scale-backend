package com.nipun.legalscale.feature.legalcasehandling.casedetail.land;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Land case detail endpoints.
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type LAND for all operations.
 *
 * Base path: /api/cases/{caseId}/land
 */
@RestController
@RequestMapping("/api/cases/{caseId}/land")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class LandCaseController {

    private final LandCaseService landCaseService;

    // ─── Feature 1: Land Reference Management ────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/land/details
     * Set or update all land reference and survey details.
     * Creates the record on first call; updates on subsequent calls.
     * Enforces uniqueness of landReferenceNumber across all cases.
     */
    @PutMapping("/details")
    public ResponseEntity<LandDetailResponse> setLandDetails(
            @PathVariable Long caseId,
            @Valid @RequestBody LandDetailRequest request) {
        return ResponseEntity.ok(landCaseService.setLandDetails(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/land
     * Full land case summary: all reference fields, full ownership chain
     * (with currentOwner pointer), and all registered deeds/plans.
     */
    @GetMapping
    public ResponseEntity<LandDetailResponse> getDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(landCaseService.getLandDetail(caseId));
    }

    // ─── Feature 2: Ownership History ────────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/land/ownership
     * Add an entry to the ownership history chain.
     * Leave ownershipEndDate null for the current owner.
     */
    @PostMapping("/ownership")
    public ResponseEntity<OwnershipRecordResponse> addOwnershipRecord(
            @PathVariable Long caseId,
            @Valid @RequestBody OwnershipRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(landCaseService.addOwnershipRecord(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/land/ownership/{recordId}
     * Update an existing ownership record.
     * Typically used to set the end date when ownership transfers to a new owner.
     */
    @PutMapping("/ownership/{recordId}")
    public ResponseEntity<OwnershipRecordResponse> updateOwnershipRecord(
            @PathVariable Long caseId,
            @PathVariable Long recordId,
            @Valid @RequestBody OwnershipRecordRequest request) {
        return ResponseEntity.ok(landCaseService.updateOwnershipRecord(caseId, recordId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/land/ownership/{recordId}
     * Remove an ownership record entered in error.
     */
    @DeleteMapping("/ownership/{recordId}")
    public ResponseEntity<Void> deleteOwnershipRecord(
            @PathVariable Long caseId,
            @PathVariable Long recordId) {
        landCaseService.deleteOwnershipRecord(caseId, recordId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 3: Deed & Plan Management ───────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/land/documents
     * Register a deed or plan entry.
     * - Provide uploadedDocumentId to link an already-uploaded file.
     * - Leave uploadedDocumentId null to record as registry-only.
     */
    @PostMapping("/documents")
    public ResponseEntity<LandDeedPlanResponse> addDeedPlan(
            @PathVariable Long caseId,
            @Valid @RequestBody LandDeedPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(landCaseService.addDeedPlan(caseId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/land/documents/{deedPlanId}
     * Remove a deed/plan registry entry.
     * Note: This does NOT delete the uploaded document file itself.
     */
    @DeleteMapping("/documents/{deedPlanId}")
    public ResponseEntity<Void> deleteDeedPlan(
            @PathVariable Long caseId,
            @PathVariable Long deedPlanId) {
        landCaseService.deleteDeedPlan(caseId, deedPlanId);
        return ResponseEntity.noContent().build();
    }
}
