package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.DecisionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InquiryDecisionResponse {

    private Long id;
    private Long caseId;
    private String decisionTitle;
    private String decisionDetails;
    private String responsibleParty;
    private LocalDate targetDate;
    private LocalDate implementedDate;
    private DecisionStatus status;
    private String notes;

    /** Finding number this decision relates to (null if inquiry-level) */
    private Integer relatedFindingNumber;
    private String relatedFindingTitle;

    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String statusUpdatedByName;
    private LocalDateTime statusUpdatedAt;

    /** True if target date has passed and status is still PENDING or IN_PROGRESS */
    private boolean overdue;
}
