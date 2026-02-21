package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.AppealJudgment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppealOutcomeRequest {

    @NotBlank(message = "Judging court / authority is required")
    private String judgingCourt;

    @NotNull(message = "Judgment date is required")
    private LocalDate judgmentDate;

    @NotNull(message = "Judgment type is required")
    private AppealJudgment judgment;

    @NotBlank(message = "Judgment summary is required")
    private String judgmentSummary;

    /** Required when judgment is REMITTED */
    private String remittalInstructions;

    /** Optional reference number of the judgment */
    private String judgmentReference;

    private String notes;
}
