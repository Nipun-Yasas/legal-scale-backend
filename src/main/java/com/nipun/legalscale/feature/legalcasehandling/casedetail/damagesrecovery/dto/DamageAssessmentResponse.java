package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.AssessmentStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.DamageCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DamageAssessmentResponse {

    private Long id;
    private Long caseId;
    private DamageCategory category;
    private String description;
    private BigDecimal estimatedValue;
    private String assessorName;
    private LocalDate assessmentDate;
    private AssessmentStatus status;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String statusUpdatedByName;
    private LocalDateTime statusUpdatedAt;
}
