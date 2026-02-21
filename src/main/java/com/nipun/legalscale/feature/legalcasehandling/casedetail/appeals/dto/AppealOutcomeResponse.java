package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.AppealJudgment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AppealOutcomeResponse {

    private Long id;
    private Long caseId;
    private String judgingCourt;
    private LocalDate judgmentDate;
    private AppealJudgment judgment;
    private String judgmentSummary;
    private String remittalInstructions;
    private String judgmentReference;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
