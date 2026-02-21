package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Used to set or update the appeal header details (original case link, filing
 * date, grounds).
 */
@Data
public class AppealDetailRequest {

    /**
     * ID of the original case within this system, if it exists here.
     * Leave null if the original case is from an external court/system.
     */
    private Long originalCaseId;

    /**
     * Free-text reference for the original case if it is external
     * (e.g. "DC/COL/2024/1234"). Used when originalCaseId is null.
     */
    private String originalCaseReference;

    @NotBlank(message = "Appeal court / authority is required")
    private String appealCourt;

    @NotNull(message = "Filing date is required")
    private LocalDate filingDate;

    @NotBlank(message = "Grounds of appeal are required")
    private String groundsOfAppeal;

    private String notes;
}
