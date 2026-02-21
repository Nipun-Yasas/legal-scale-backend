package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.enums.RecoveryType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single recovery event — either a partial payment or a full
 * settlement.
 * Many transactions can exist per MoneyRecoveryDetail.
 */
@Entity
@Table(name = "recovery_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "money_recovery_detail_id", nullable = false)
    private MoneyRecoveryDetail moneyRecoveryDetail;

    /** Amount recovered in this transaction */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Whether this is a partial payment or the final full settlement */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecoveryType recoveryType;

    /** The actual date the payment/recovery was received */
    @Column(nullable = false)
    private LocalDate transactionDate;

    /** Optional reference, cheque number, notes, etc. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Who recorded this transaction */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    /** When this transaction was entered into the system */
    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
