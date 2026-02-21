package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.DecisionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DecisionStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private DecisionStatus status;

    /** Required when status is IMPLEMENTED */
    private LocalDate implementedDate;

    private String notes;
}
