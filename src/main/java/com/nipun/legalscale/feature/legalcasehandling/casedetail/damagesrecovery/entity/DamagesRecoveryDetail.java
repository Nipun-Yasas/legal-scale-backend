package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for a DAMAGES_RECOVERY case (1:1 with InitialCaseEntity).
 * Owns damage assessments, compensation payments, and the optional
 * settlement agreement.
 */
@Entity
@Table(name = "damages_recovery_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamagesRecoveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    /** Total compensation amount being claimed in this case */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCompensationClaimed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;

    // ─── Sub-feature: Damage Assessments ─────────────────────────────────────────

    @OneToMany(mappedBy = "damagesRecoveryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DamageAssessment> assessments = new ArrayList<>();

    // ─── Sub-feature: Compensation Payments ──────────────────────────────────────

    @OneToMany(mappedBy = "damagesRecoveryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompensationPayment> compensationPayments = new ArrayList<>();

    // ─── Sub-feature: Settlement Agreement ───────────────────────────────────────

    /** Optional – created when settlement negotiations begin */
    @OneToOne(mappedBy = "damagesRecoveryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private SettlementAgreement settlementAgreement;
}
