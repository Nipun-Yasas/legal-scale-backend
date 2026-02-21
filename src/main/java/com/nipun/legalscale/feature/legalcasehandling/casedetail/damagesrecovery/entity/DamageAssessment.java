package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.AssessmentStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.DamageCategory;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a single damage assessment — the valuation of a particular type of
 * damage by a named assessor. Multiple assessments can exist per case.
 */
@Entity
@Table(name = "damage_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamageAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "damages_recovery_detail_id", nullable = false)
    private DamagesRecoveryDetail damagesRecoveryDetail;

    /** Category of damage being assessed */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageCategory category;

    /** Detailed description of the damage */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Monetary value placed on this damage by the assessor */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    /** Name of the expert / assessor who evaluated this damage */
    @Column(nullable = false)
    private String assessorName;

    /** Date the assessment was carried out */
    @Column(nullable = false)
    private LocalDate assessmentDate;

    /** Current status of this assessment (PENDING / COMPLETED / DISPUTED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssessmentStatus status = AssessmentStatus.PENDING;

    /** Additional notes, references, or justification */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** The officer/supervisor who entered this record */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    /** The officer/supervisor who last changed the status */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_updated_by_id")
    private UserEntity statusUpdatedBy;

    private LocalDateTime statusUpdatedAt;
}
