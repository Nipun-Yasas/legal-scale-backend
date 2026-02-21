package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.PanelMemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PanelMemberResponse {

    private Long id;
    private Long caseId;
    private String memberName;
    private String designation;
    private String department;
    private PanelMemberRole role;
    private LocalDate appointedDate;
    private String contactDetails;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
}
