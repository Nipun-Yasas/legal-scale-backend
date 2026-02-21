package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Used to create or update the Money Recovery Detail (claim amount) for a case.
 */
@Data
public class MoneyRecoveryDetailRequest {

    @NotNull(message = "Total claim amount is required")
    @DecimalMin(value = "0.01", message = "Total claim amount must be greater than zero")
    private BigDecimal totalClaimAmount;
}
