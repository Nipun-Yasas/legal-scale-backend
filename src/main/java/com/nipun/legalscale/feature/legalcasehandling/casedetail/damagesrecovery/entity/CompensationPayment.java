package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity;

import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a single compensation payment received against a damages claim.
 * Multiple payments can exist per case (instalments or partial settlements).
 */
@Entity
@Table(name = "compensation_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompensationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "damages_recovery_detail_id", nullable = false)
    private DamagesRecoveryDetail damagesRecoveryDetail;

    /** Amount received in this payment */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Date the payment was actually received */
    @Column(nullable = false)
    private LocalDate paymentDate;

    /** Reference number, cheque number, bank transfer ID, etc. */
    @Column(nullable = false)
    private String paymentReference;

    /** Optional notes */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
