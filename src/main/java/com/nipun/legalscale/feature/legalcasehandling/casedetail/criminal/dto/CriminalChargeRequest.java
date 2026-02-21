package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.ChargeStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.PleaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CriminalChargeRequest {

    @NotBlank(message = "Statute name is required")
    private String statute;

    @NotBlank(message = "Section number is required")
    private String section;

    @NotBlank(message = "Offence name is required")
    private String offenceName;

    @NotBlank(message = "Offence description is required")
    private String offenceDescription;

    private String maximumPenalty;

    /** Default to NO_PLEA_ENTERED if not supplied */
    private PleaType plea;

    /** Default to PENDING if not supplied */
    private ChargeStatus status;

    private String outcomeDetails;
    private String notes;
}
