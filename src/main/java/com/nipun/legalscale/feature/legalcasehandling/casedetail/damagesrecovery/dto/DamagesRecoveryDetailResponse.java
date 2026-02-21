package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete Damages Recovery summary for a case.
 */
@Data
@Builder
public class DamagesRecoveryDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    /** Total compensation being claimed */
    private BigDecimal totalCompensationClaimed;

    /** Sum of all estimated values across all damage assessments */
    private BigDecimal totalAssessedValue;

    /** Sum of all compensation payments received so far */
    private BigDecimal totalCompensationReceived;

    /** Outstanding balance = claimed − received (never below 0) */
    private BigDecimal outstandingBalance;

    /** True when outstanding balance is 0 */
    private boolean fullyCompensated;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Sub-feature details
    // ──────────────────────────────────────────────────────

    private List<DamageAssessmentResponse> assessments;
    private List<CompensationPaymentResponse> compensationPayments;

    /** Null if no settlement has been proposed yet */
    private SettlementAgreementResponse settlementAgreement;
}
