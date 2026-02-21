package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.DamageCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DamageAssessmentRequest {

    @NotNull(message = "Damage category is required")
    private DamageCategory category;

    @NotBlank(message = "Damage description is required")
    private String description;

    @NotNull(message = "Estimated value is required")
    @DecimalMin(value = "0.01", message = "Estimated value must be greater than zero")
    private BigDecimal estimatedValue;

    @NotBlank(message = "Assessor name is required")
    private String assessorName;

    @NotNull(message = "Assessment date is required")
    private LocalDate assessmentDate;

    /** Optional additional notes */
    private String notes;
}
