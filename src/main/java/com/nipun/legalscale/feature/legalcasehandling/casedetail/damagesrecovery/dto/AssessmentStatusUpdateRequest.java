package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.AssessmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssessmentStatusUpdateRequest {

    @NotNull(message = "New status is required (PENDING, COMPLETED, or DISPUTED)")
    private AssessmentStatus status;

    /** Optional reason for status change */
    private String notes;
}
