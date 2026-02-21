package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.PanelMemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PanelMemberRequest {

    @NotBlank(message = "Member name is required")
    private String memberName;

    @NotBlank(message = "Designation is required")
    private String designation;

    private String department;

    @NotNull(message = "Role is required")
    private PanelMemberRole role;

    private LocalDate appointedDate;
    private String contactDetails;
    private String notes;
}
