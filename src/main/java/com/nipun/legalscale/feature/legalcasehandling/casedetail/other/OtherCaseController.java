package com.nipun.legalscale.feature.legalcasehandling.casedetail.other;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * OTHER case detail endpoints covering:
 * Feature 1 – Configurable Case Attributes (EAV key-value fields with type,
 * category, ordering)
 * Feature 2 – Custom Document Templates (inline content with {{placeholders}}
 * and/or uploaded file)
 *
 * Available to LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * Case must be ACTIVE and of type OTHER for all operations.
 *
 * Base path: /api/cases/{caseId}/other
 */
@RestController
@RequestMapping("/api/cases/{caseId}/other")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class OtherCaseController {

    private final OtherCaseService otherCaseService;

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /**
     * PUT /api/cases/{caseId}/other/details
     * Set or update the case nature label and description.
     * Creates the record on first call; updates on subsequent calls.
     */
    @PutMapping("/details")
    public ResponseEntity<OtherCaseDetailResponse> setCaseDetails(
            @PathVariable Long caseId,
            @Valid @RequestBody OtherCaseDetailRequest request) {
        return ResponseEntity.ok(otherCaseService.setCaseDetails(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/other
     * Full summary: case nature, all attributes (flat list + grouped map),
     * and all templates with status counts.
     */
    @GetMapping
    public ResponseEntity<OtherCaseDetailResponse> getCaseDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(otherCaseService.getCaseDetail(caseId));
    }

    // ─── Feature 1: Configurable Attributes ──────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/other/attributes
     * Add a configurable attribute.
     * - attributeName must be unique within the case (used as the key)
     * - dataType hints to the UI how to render / validate the value
     * - category groups attributes into labelled sections
     * - displayOrder controls rendering sequence within the category
     */
    @PostMapping("/attributes")
    public ResponseEntity<CaseAttributeResponse> addAttribute(
            @PathVariable Long caseId,
            @Valid @RequestBody CaseAttributeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(otherCaseService.addAttribute(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/other/attributes/{attributeId}
     * Update an existing attribute (key, label, value, type, category, order).
     */
    @PutMapping("/attributes/{attributeId}")
    public ResponseEntity<CaseAttributeResponse> updateAttribute(
            @PathVariable Long caseId,
            @PathVariable Long attributeId,
            @Valid @RequestBody CaseAttributeRequest request) {
        return ResponseEntity.ok(otherCaseService.updateAttribute(caseId, attributeId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/other/attributes/{attributeId}
     * Remove a configurable attribute.
     */
    @DeleteMapping("/attributes/{attributeId}")
    public ResponseEntity<Void> deleteAttribute(
            @PathVariable Long caseId,
            @PathVariable Long attributeId) {
        otherCaseService.deleteAttribute(caseId, attributeId);
        return ResponseEntity.noContent().build();
    }

    // ─── Feature 2: Document Templates ───────────────────────────────────────────

    /**
     * POST /api/cases/{caseId}/other/templates
     * Create a new document template.
     * Rules:
     * - At least one of templateContent (inline) or uploadedTemplateDocumentId must
     * be provided.
     * - Inline content may contain {{placeholders}} e.g. {{caseTitle}},
     * {{referenceNumber}}.
     * - New templates start in DRAFT status.
     */
    @PostMapping("/templates")
    public ResponseEntity<CaseDocumentTemplateResponse> addTemplate(
            @PathVariable Long caseId,
            @Valid @RequestBody CaseDocumentTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(otherCaseService.addTemplate(caseId, request));
    }

    /**
     * PUT /api/cases/{caseId}/other/templates/{templateId}
     * Update an existing template's content, version, or linked file.
     * Tip: ARCHIVE the old template and create a new version rather than
     * overwriting an ACTIVE template that has already been used.
     */
    @PutMapping("/templates/{templateId}")
    public ResponseEntity<CaseDocumentTemplateResponse> updateTemplate(
            @PathVariable Long caseId,
            @PathVariable Long templateId,
            @Valid @RequestBody CaseDocumentTemplateRequest request) {
        return ResponseEntity.ok(otherCaseService.updateTemplate(caseId, templateId, request));
    }

    /**
     * PATCH /api/cases/{caseId}/other/templates/{templateId}/status
     * Change the lifecycle status: DRAFT → ACTIVE → ARCHIVED.
     */
    @PatchMapping("/templates/{templateId}/status")
    public ResponseEntity<CaseDocumentTemplateResponse> updateTemplateStatus(
            @PathVariable Long caseId,
            @PathVariable Long templateId,
            @Valid @RequestBody TemplateStatusUpdateRequest request) {
        return ResponseEntity.ok(otherCaseService.updateTemplateStatus(caseId, templateId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/other/templates/{templateId}
     * Remove a template. Note: this does NOT delete the uploaded document file.
     */
    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long caseId,
            @PathVariable Long templateId) {
        otherCaseService.deleteTemplate(caseId, templateId);
        return ResponseEntity.noContent().build();
    }
}
