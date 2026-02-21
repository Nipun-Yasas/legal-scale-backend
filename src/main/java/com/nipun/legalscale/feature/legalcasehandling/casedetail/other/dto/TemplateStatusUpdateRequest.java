package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TemplateStatusUpdateRequest {

    @NotNull(message = "Status is required (DRAFT, ACTIVE, or ARCHIVED)")
    private TemplateStatus status;

    private String notes;
}
