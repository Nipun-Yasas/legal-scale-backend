package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.FindingSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InquiryFindingRequest {

    @NotBlank(message = "Finding title is required")
    private String findingTitle;

    @NotBlank(message = "Finding description is required")
    private String findingDescription;

    @NotNull(message = "Severity is required")
    private FindingSeverity severity;

    @NotBlank(message = "Recommendation is required")
    private String recommendation;

    private String notes;
}
