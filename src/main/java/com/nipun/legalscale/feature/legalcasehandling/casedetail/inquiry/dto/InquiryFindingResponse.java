package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.FindingSeverity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InquiryFindingResponse {

    private Long id;
    private Long caseId;
    private Integer findingNumber;
    private String findingTitle;
    private String findingDescription;
    private FindingSeverity severity;
    private String recommendation;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
