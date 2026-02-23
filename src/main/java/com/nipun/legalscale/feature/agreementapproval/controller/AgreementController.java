package com.nipun.legalscale.feature.agreementapproval.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nipun.legalscale.feature.agreementapproval.dto.AgreementResponse;
import com.nipun.legalscale.feature.agreementapproval.dto.CreateAgreementRequest;
import com.nipun.legalscale.feature.agreementapproval.service.AgreementService;
import com.nipun.legalscale.feature.agreementapproval.dto.ReviewAgreementRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/agreements")
@RequiredArgsConstructor
public class AgreementController {

    private final AgreementService agreementService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AgreementResponse> createAgreement(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "document", required = false) MultipartFile document) {

        CreateAgreementRequest request;
        try {
            request = objectMapper.readValue(dataJson, CreateAgreementRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in 'data' field: " + e.getOriginalMessage());
        }

        Set<ConstraintViolation<CreateAgreementRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agreementService.createAgreement(request, document));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AgreementResponse>> getAllAgreements() {
        return ResponseEntity.ok(agreementService.getAllAgreements());
    }

    @GetMapping("/my")
    public ResponseEntity<List<AgreementResponse>> getMyAgreements() {
        return ResponseEntity.ok(agreementService.getMyAgreements());
    }

    @PostMapping("/{id}/request-review")
    public ResponseEntity<AgreementResponse> requestReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewAgreementRequest request) {
        return ResponseEntity.ok(agreementService.requestReview(id, request));
    }

    @PostMapping(value = "/{id}/revisions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AgreementResponse> uploadRevision(
            @PathVariable Long id,
            @RequestParam("revisionNotes") String revisionNotes,
            @RequestPart("document") MultipartFile document) {
        return ResponseEntity.ok(agreementService.uploadRevision(id, revisionNotes, document));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AgreementResponse> getAgreementById(@PathVariable Long id) {
        return ResponseEntity.ok(agreementService.getAgreementById(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<AgreementResponse> addComment(
            @PathVariable Long id,
            @RequestParam("commentText") String commentText) {
        return ResponseEntity.ok(agreementService.addComment(id, commentText));
    }

}
