package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailResponse;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionResponse;

public interface MoneyRecoveryService {

    /**
     * Set or update the total claim amount for an ACTIVE MONEY_RECOVERY case.
     * Creates the MoneyRecoveryDetail record the first time; updates it on
     * subsequent calls.
     */
    MoneyRecoveryDetailResponse setClaimAmount(Long caseId, MoneyRecoveryDetailRequest request);

    /**
     * Get the full money recovery summary for a case, including all transactions
     * and the calculated outstanding balance.
     */
    MoneyRecoveryDetailResponse getMoneyRecoveryDetail(Long caseId);

    /**
     * Record a recovery transaction (partial or full payment received).
     * The detail record must exist (i.e. claim amount must have been set first).
     */
    RecoveryTransactionResponse recordRecovery(Long caseId, RecoveryTransactionRequest request);

    /**
     * Delete a mistakenly recorded transaction.
     */
    void deleteTransaction(Long caseId, Long transactionId);
}
