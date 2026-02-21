package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingOutcome;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourtHearingRequest {

    @NotNull(message = "Hearing date is required")
    private LocalDate hearingDate;

    @NotNull(message = "Hearing type is required")
    private HearingType hearingType;

    private String presidingJudge;

    @NotBlank(message = "Proceedings summary is required")
    private String proceedingsSummary;

    @NotNull(message = "Hearing outcome is required")
    private HearingOutcome outcome;

    /**
     * Required when outcome is ADJOURNED — the date to which the case was
     * adjourned.
     */
    private LocalDate nextHearingDate;

    private String nextHearingPurpose;
    private String notes;
}
