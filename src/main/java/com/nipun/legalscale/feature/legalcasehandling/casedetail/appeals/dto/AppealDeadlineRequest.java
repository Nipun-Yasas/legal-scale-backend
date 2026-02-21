package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppealDeadlineRequest {

    @NotNull(message = "Deadline type is required")
    private DeadlineType deadlineType;

    @NotNull(message = "Deadline date is required")
    private LocalDate deadlineDate;

    private String notes;
}
