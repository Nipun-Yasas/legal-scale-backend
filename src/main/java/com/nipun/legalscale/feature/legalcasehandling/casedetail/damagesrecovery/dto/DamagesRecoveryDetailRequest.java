package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DamagesRecoveryDetailRequest {

    @NotNull(message = "Total compensation claimed is required")
    @DecimalMin(value = "0.01", message = "Total compensation claimed must be greater than zero")
    private BigDecimal totalCompensationClaimed;
}
