package com.nipun.legalscale.feature.legalcasehandling.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CaseCommentResponse {

    private Long id;
    private Long caseId;
    private String commentedByName;
    private String commentedByEmail;
    private String comment;
    private LocalDateTime commentedAt;
}
