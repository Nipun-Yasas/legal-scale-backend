package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InquiryDecisionRequest {

    @NotBlank(message = "Decision title is required")
    private String decisionTitle;

    @NotBlank(message = "Decision details are required")
    private String decisionDetails;

    @NotBlank(message = "Responsible party is required")
    private String responsibleParty;

    private LocalDate targetDate;

    /**
     * Optional: link this decision to a specific finding within this inquiry.
     * Leave null if the decision applies to the inquiry overall.
     */
    private Long relatedFindingId;

    private String notes;
}
