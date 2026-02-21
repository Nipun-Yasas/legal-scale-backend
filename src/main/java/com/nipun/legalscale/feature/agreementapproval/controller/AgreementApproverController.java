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
@RequestMapping("/api/agreements/approver")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('AGREEMENT_APPROVER')")
public class AgreementApproverController {

    private final AgreementService agreementService;

    @GetMapping("/pending")
    public ResponseEntity<List<AgreementResponse>> getAgreementsForApproval() {
        return ResponseEntity.ok(agreementService.getAgreementsForApproval());
    }

    @PostMapping("/{id}/approve-reject")
    public ResponseEntity<AgreementResponse> approveOrRejectAgreement(
            @PathVariable Long id,
            @RequestBody @Valid ReviewAgreementRequest request) {
        return ResponseEntity.ok(agreementService.approveOrReject(id, request));
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<AgreementResponse> executeAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(agreementService.executeAgreement(id));
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<AgreementResponse> digitallySignAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(agreementService.digitallySignAgreement(id));
    }
}
