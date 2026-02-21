package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtherCaseDetailRequest {

    @NotBlank(message = "Case nature label is required (e.g. 'Contempt of Court', 'Injunction Application')")
    private String caseNature;

    private String description;
    private String notes;
}
