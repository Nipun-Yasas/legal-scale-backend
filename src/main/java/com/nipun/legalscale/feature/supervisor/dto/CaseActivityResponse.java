package com.nipun.legalscale.feature.supervisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseActivityResponse {
    private String type;
    private Long caseId;
    private String referenceNumber;
    private String caseTitle;
    private String authorName;
    private String authorEmail;
    private String content;
    private LocalDateTime timestamp;
}
