package com.nipun.legalscale.feature.agreementapproval.dto;

import com.nipun.legalscale.feature.agreementapproval.enums.AgreementStatus;
import com.nipun.legalscale.feature.agreementapproval.enums.AgreementType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AgreementResponse {
    private Long id;
    private String title;
    private AgreementType type;
    private String parties;
    private BigDecimal value;
    private LocalDate startDate;
    private LocalDate endDate;
    private AgreementStatus status;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long reviewerId;
    private String reviewerName;
    private Long approverId;
    private String approverName;
    private Long linkedCaseId;
    private String approvalRemarks;
    private Boolean isDigitallySigned;
    private List<AgreementVersionResponse> versions;
    private List<AgreementCommentResponse> comments;
}
