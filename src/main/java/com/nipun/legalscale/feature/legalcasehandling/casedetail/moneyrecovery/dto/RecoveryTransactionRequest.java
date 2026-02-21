package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.enums.RecoveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Used to record a single partial or full recovery payment.
 */
@Data
public class RecoveryTransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Recovery type is required (PARTIAL or FULL)")
    private RecoveryType recoveryType;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    /** Optional reference number, cheque number, or additional notes */
    private String notes;
}
