package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CompensationPaymentResponse {

    private Long id;
    private Long caseId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentReference;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
}
