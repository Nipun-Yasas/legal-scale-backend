package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Full Money Recovery summary for a case, including all transactions
 * and the calculated outstanding balance.
 */
@Data
@Builder
public class MoneyRecoveryDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    /** The total amount claimed in this case */
    private BigDecimal totalClaimAmount;

    /** Sum of all recorded recovery transactions */
    private BigDecimal totalRecoveredAmount;

    /**
     * Outstanding balance = totalClaimAmount − totalRecoveredAmount
     * Never goes below 0.
     */
    private BigDecimal outstandingBalance;

    /** True when outstandingBalance == 0 */
    private boolean fullyRecovered;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    private List<RecoveryTransactionResponse> transactions;
}
