package com.nipun.legalscale.feature.legalcasehandling;

import com.nipun.legalscale.feature.legalcasehandling.dto.CaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Open to any authenticated user.
 *
 * Attachments can be managed individually via the /{id}/attachments endpoints.
 */
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    /**
     * GET /api/cases/{id}
     * Retrieve full case details including the audit trail and all attachments.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @GetMapping("/status-counts")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGEMENT')")
    public ResponseEntity<java.util.Map<String, Long>> getCaseStatusCounts() {
        return ResponseEntity.ok(caseService.getCaseStatusCounts());
    }

    @GetMapping("/officer-counts")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('MANAGEMENT', 'SYSTEM_ADMIN')")
    public ResponseEntity<java.util.Map<String, Long>> getAssignedCaseCountsPerOfficer() {
        return ResponseEntity.ok(caseService.getAssignedCaseCountsPerOfficer());
    }

    // ─── Standalone Attachment Management ────────────────────────────────────────

    @PostMapping(value = "/{id}/attachments", consumes = "multipart/form-data")
    public ResponseEntity<CaseResponse> attachDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(caseService.attachDocument(id, file));
    }

    /**
     * DELETE /api/cases/{id}/attachments/{documentId}
     *
     * Unlink a supporting document from the case.
     * The document record stays in the documents table; only the join-table row is
     * removed.
     */
    @DeleteMapping("/{id}/attachments/{documentId}")
    public ResponseEntity<CaseResponse> removeAttachment(
            @PathVariable Long id,
            @PathVariable Long documentId) {
        return ResponseEntity.ok(caseService.removeAttachment(id, documentId));
    }
}
