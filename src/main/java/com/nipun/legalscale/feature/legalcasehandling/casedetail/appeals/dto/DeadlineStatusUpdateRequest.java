package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeadlineStatusUpdateRequest {

    @NotNull(message = "New status is required (PENDING, MET, MISSED, or EXTENDED)")
    private DeadlineStatus status;

    /**
     * Required when status is EXTENDED — the new extended deadline date.
     */
    private LocalDate extendedDeadlineDate;

    private String notes;
}
