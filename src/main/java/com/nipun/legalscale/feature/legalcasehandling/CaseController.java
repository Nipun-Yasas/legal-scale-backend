package com.nipun.legalscale.feature.legalcasehandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nipun.legalscale.feature.legalcasehandling.dto.CaseResponse;
import com.nipun.legalscale.feature.legalcasehandling.dto.CreateCaseRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Open to any authenticated user.
 *
 * Case creation accepts multipart/form-data so that supporting documents can
 * optionally be attached at the same time as the case fields.
 * Attachments can also be managed individually after creation via the
 * /{id}/attachments endpoints.
 */
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * POST /api/cases
     *
     * Creates a new case and optionally attaches supporting documents in one shot.
     *
     * Content-Type: multipart/form-data
     * Part "data" – JSON string for the case fields (plain text, no Content-Type
     * needed)
     * Part "attachments" – zero or more files (optional)
     *
     * How to send in Postman (Body → form-data):
     * Key: data | Type: Text | Value: {"caseTitle":"...","caseType":"LAND",...}
     * Key: attachments | Type: File | Value: <select file> ← repeat for each file
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaseResponse> createCase(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        // Deserialize the JSON string manually (avoids Content-Type: application/json
        // requirement on the part)
        CreateCaseRequest request;
        try {
            request = objectMapper.readValue(dataJson, CreateCaseRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in 'data' field: " + e.getOriginalMessage());
        }

        // Run Bean Validation manually since @Valid doesn't apply to @RequestParam
        // strings
        Set<ConstraintViolation<CreateCaseRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        List<MultipartFile> files = (attachments != null) ? attachments : Collections.emptyList();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(caseService.createCase(request, files));
    }

    /**
     * GET /api/cases/{id}
     * Retrieve full case details including the audit trail and all attachments.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    // ─── Standalone Attachment Management ────────────────────────────────────────

    /**
     * POST /api/cases/{id}/attachments
     *
     * Upload and attach a single supporting document to an existing case.
     *
     * Content-Type: multipart/form-data
     * Part "file" – the file to upload
     */
    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
