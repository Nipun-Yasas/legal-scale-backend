package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailResponse;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Money Recovery case detail endpoints.
 *
 * Available to both LEGAL_OFFICER and LEGAL_SUPERVISOR.
 * The case must be ACTIVE and of type MONEY_RECOVERY for any operation to
 * succeed.
 *
 * Base path: /api/cases/{caseId}/money-recovery
 */
@RestController
@RequestMapping("/api/cases/{caseId}/money-recovery")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEGAL_OFFICER', 'LEGAL_SUPERVISOR')")
public class MoneyRecoveryController {

    private final MoneyRecoveryService moneyRecoveryService;

    /**
     * PUT /api/cases/{caseId}/money-recovery/claim
     *
     * Set or update the total claim amount for a MONEY_RECOVERY case.
     * Creates the MoneyRecoveryDetail record on first call; updates it on
     * subsequent calls.
     */
    @PutMapping("/claim")
    public ResponseEntity<MoneyRecoveryDetailResponse> setClaimAmount(
            @PathVariable Long caseId,
            @Valid @RequestBody MoneyRecoveryDetailRequest request) {
        return ResponseEntity.ok(moneyRecoveryService.setClaimAmount(caseId, request));
    }

    /**
     * GET /api/cases/{caseId}/money-recovery
     *
     * Get the full money recovery summary:
     * - Total claim amount
     * - Total recovered so far
     * - Outstanding balance (calculated)
     * - Fully recovered flag
     * - All transaction history
     */
    @GetMapping
    public ResponseEntity<MoneyRecoveryDetailResponse> getDetail(@PathVariable Long caseId) {
        return ResponseEntity.ok(moneyRecoveryService.getMoneyRecoveryDetail(caseId));
    }

    /**
     * POST /api/cases/{caseId}/money-recovery/transactions
     *
     * Record a recovery transaction (partial or full payment received).
     * - Claim amount must be set first.
     * - Amount must not push total recovered beyond the claim amount.
     */
    @PostMapping("/transactions")
    public ResponseEntity<RecoveryTransactionResponse> recordRecovery(
            @PathVariable Long caseId,
            @Valid @RequestBody RecoveryTransactionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(moneyRecoveryService.recordRecovery(caseId, request));
    }

    /**
     * DELETE /api/cases/{caseId}/money-recovery/transactions/{transactionId}
     *
     * Remove a mistakenly recorded transaction.
     * Only allowed while the case is ACTIVE.
     */
    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long caseId,
            @PathVariable Long transactionId) {
        moneyRecoveryService.deleteTransaction(caseId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
