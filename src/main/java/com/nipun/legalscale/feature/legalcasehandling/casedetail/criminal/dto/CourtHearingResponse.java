package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingOutcome;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CourtHearingResponse {

    private Long id;
    private Long caseId;
    private LocalDate hearingDate;
    private HearingType hearingType;
    private String presidingJudge;
    private String proceedingsSummary;
    private HearingOutcome outcome;
    private LocalDate nextHearingDate;
    private String nextHearingPurpose;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
