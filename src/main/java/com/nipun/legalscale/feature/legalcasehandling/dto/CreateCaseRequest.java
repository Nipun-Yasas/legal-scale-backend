package com.nipun.legalscale.feature.legalcasehandling.dto;

import com.nipun.legalscale.feature.legalcasehandling.enums.CaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCaseRequest {

    @NotBlank(message = "Case title is required")
    private String caseTitle;

    @NotNull(message = "Case type is required")
    private CaseType caseType;

    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    @NotBlank(message = "Parties involved is required")
    private String partiesInvolved;

    @NotBlank(message = "Nature of case is required")
    private String natureOfCase;

    @NotNull(message = "Date of occurrence / filing is required")
    private LocalDate dateOfOccurrenceOrFiling;

    @NotBlank(message = "Court / authority is required")
    private String courtOrAuthority;

    /** Optional — only applicable when financial loss is involved */
    private BigDecimal financialExposure;

    @NotBlank(message = "Summary of facts is required")
    private String summaryOfFacts;
}
