package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One-to-one with InitialCaseEntity for MONEY_RECOVERY type cases.
 * Holds the total claim amount and owns the collection of recovery
 * transactions.
 *
 * Extension pattern: future case types (e.g. LandCaseDetail,
 * CriminalCaseDetail)
 * will follow this same pattern with their own entity and repository.
 */
@Entity
@Table(name = "money_recovery_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyRecoveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The active MONEY_RECOVERY case this detail belongs to.
     * Unique constraint enforces one-to-one at the DB level.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    /** Total amount being claimed in this case */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalClaimAmount;

    /** Who set/last updated the claim amount */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;

    /** Individual recovery transactions (partial or full payments received) */
    @OneToMany(mappedBy = "moneyRecoveryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecoveryTransaction> transactions = new ArrayList<>();
}
