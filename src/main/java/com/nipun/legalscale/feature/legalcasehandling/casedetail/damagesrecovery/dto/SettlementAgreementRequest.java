package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SettlementAgreementRequest {

    @NotNull(message = "Agreed amount is required")
    @DecimalMin(value = "0.01", message = "Agreed amount must be greater than zero")
    private BigDecimal agreedAmount;

    @NotNull(message = "Settlement date is required")
    private LocalDate settlementDate;

    @NotBlank(message = "Settlement terms are required")
    private String terms;

    /** Optional additional notes, court order references, etc. */
    private String notes;
}
