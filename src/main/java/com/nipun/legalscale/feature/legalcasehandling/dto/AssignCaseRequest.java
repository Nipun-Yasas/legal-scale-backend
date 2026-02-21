package com.nipun.legalscale.feature.legalcasehandling.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignCaseRequest {

    @NotNull(message = "Officer ID is required")
    private Long officerId;
}
