package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.SettlementStatus;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a settlement agreement for a DAMAGES_RECOVERY case.
 * There is at most one settlement per case (1:1 with DamagesRecoveryDetail).
 * The status progresses: PROPOSED → AGREED / REJECTED → EXECUTED
 */
@Entity
@Table(name = "settlement_agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "damages_recovery_detail_id", nullable = false, unique = true)
    private DamagesRecoveryDetail damagesRecoveryDetail;

    /** The amount both parties agree to settle for */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal agreedAmount;

    /** Date the settlement was proposed or agreed */
    @Column(nullable = false)
    private LocalDate settlementDate;

    /** Full text of the settlement terms */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String terms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PROPOSED;

    /** Additional notes or references (e.g. court order number) */
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit
    // ────────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by_id", nullable = false)
    private UserEntity proposedBy;

    @Column(nullable = false)
    private LocalDateTime proposedAt;

    /** Populated when status moves to AGREED or EXECUTED */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_updated_by_id")
    private UserEntity statusUpdatedBy;

    private LocalDateTime statusUpdatedAt;
}
