package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AppealDeadlineResponse {

    private Long id;
    private Long caseId;
    private DeadlineType deadlineType;
    private LocalDate deadlineDate;
    private DeadlineStatus status;
    private LocalDate extendedDeadlineDate;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String statusUpdatedByName;
    private LocalDateTime statusUpdatedAt;

    /** True if deadline is PENDING and the deadline date has already passed */
    private boolean overdue;
}
