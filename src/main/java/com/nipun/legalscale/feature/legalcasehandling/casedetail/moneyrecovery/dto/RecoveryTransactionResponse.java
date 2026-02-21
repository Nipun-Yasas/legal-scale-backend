package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.enums.RecoveryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RecoveryTransactionResponse {

    private Long id;
    private Long caseId;
    private BigDecimal amount;
    private RecoveryType recoveryType;
    private LocalDate transactionDate;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
}
