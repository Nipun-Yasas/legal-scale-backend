package com.nipun.legalscale.feature.agreementapproval.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AgreementCommentResponse {
    private Long id;
    private Long agreementId;
    private Long commentedById;
    private String commentedByName;
    private String commentText;
    private LocalDateTime createdAt;
}
