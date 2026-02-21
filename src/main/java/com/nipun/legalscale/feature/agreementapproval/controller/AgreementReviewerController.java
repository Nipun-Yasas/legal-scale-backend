package com.nipun.legalscale.feature.agreementapproval.controller;

import com.nipun.legalscale.feature.agreementapproval.dto.AgreementResponse;
import com.nipun.legalscale.feature.agreementapproval.dto.ReviewAgreementRequest;
import com.nipun.legalscale.feature.agreementapproval.service.AgreementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agreements/reviewer")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('AGREEMENT_REVIEWER')")
public class AgreementReviewerController {

    private final AgreementService agreementService;

    @GetMapping("/pending")
    public ResponseEntity<List<AgreementResponse>> getAgreementsForReview() {
        return ResponseEntity.ok(agreementService.getAgreementsForReview());
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<AgreementResponse> reviewAgreement(
            @PathVariable Long id,
            @RequestBody @Valid ReviewAgreementRequest request) {
        return ResponseEntity.ok(agreementService.reviewAgreement(id, request));
    }
}
