package com.nipun.legalscale.feature.supervisor;

import com.nipun.legalscale.feature.legalcasehandling.CaseService;
import com.nipun.legalscale.feature.legalcasehandling.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Supervisor-only endpoints.
 * Role guard: LEGAL_SUPERVISOR
 */
@RestController
@RequestMapping("/api/supervisor/cases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LEGAL_SUPERVISOR')")
public class SupervisorController {

    private final CaseService caseService;

    /**
     * GET /api/supervisor/cases/new
     * View all cases with status NEW that are awaiting review.
     */
    @GetMapping("/new")
    public ResponseEntity<List<CaseResponse>> getNewCases() {
        return ResponseEntity.ok(caseService.getAllNewCases());
    }

    /**
     * GET /api/supervisor/cases
     * View all cases regardless of status.
     */
    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    /**
     * GET /api/supervisor/cases/{id}
     * View details of a specific case.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    /**
     * POST /api/supervisor/cases/{id}/assign
     * Assign a case to a Legal Officer.
     * This records the supervisor, the assigned officer, and the timestamp.
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<CaseResponse> assignCase(
            @PathVariable Long id,
            @Valid @RequestBody AssignCaseRequest request) {
        return ResponseEntity.ok(caseService.assignCaseToOfficer(id, request));
    }

    /**
     * PATCH /api/supervisor/cases/{id}/status
     * Change the status of a case.
     * When approving (ACTIVE), approvedBy / approvedAt are recorded.
     * When closing (CLOSED), closingRemarks are mandatory, closedBy / closedAt are
     * recorded.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CaseResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request) {
        return ResponseEntity.ok(caseService.supervisorUpdateStatus(id, request));
    }

    /**
     * POST /api/supervisor/cases/{id}/comments
     * Add a comment to a case.
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CaseCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(caseService.supervisorAddComment(id, request));
    }
}
