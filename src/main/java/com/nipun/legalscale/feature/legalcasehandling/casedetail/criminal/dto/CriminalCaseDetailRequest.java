package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CriminalCaseDetailRequest {

    @NotBlank(message = "Accused name is required")
    private String accusedName;

    private String accusedIdNumber;
    private String accusedAddress;

    @NotBlank(message = "Court name is required")
    private String court;

    private String courtCaseNumber;
    private String presidingJudge;
    private LocalDate courtFilingDate;
    private String notes;
}
