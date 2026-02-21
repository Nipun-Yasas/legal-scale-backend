package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.SettlementStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SettlementAgreementResponse {

    private Long id;
    private Long caseId;
    private BigDecimal agreedAmount;
    private LocalDate settlementDate;
    private String terms;
    private SettlementStatus status;
    private String notes;
    private String proposedByName;
    private String proposedByEmail;
    private LocalDateTime proposedAt;
    private String statusUpdatedByName;
    private LocalDateTime statusUpdatedAt;
}
