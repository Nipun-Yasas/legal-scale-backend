package com.nipun.legalscale.feature.legalcasehandling.dto;

import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCaseStatusRequest {

    @NotNull(message = "Status is required")
    private CaseStatus status;

    /**
     * Required when status is CLOSED.
     */
    private String closingRemarks;
}
