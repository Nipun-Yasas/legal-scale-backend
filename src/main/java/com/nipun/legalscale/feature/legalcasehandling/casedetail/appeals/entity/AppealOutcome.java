package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.AppealJudgment;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records the final outcome / judgment of an appeal.
 * One per AppealDetail (1:1).
 *
 * Feature 3 – Appeal Outcome Recording.
 */
@Entity
@Table(name = "appeal_outcomes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_detail_id", nullable = false, unique = true)
    private AppealDetail appealDetail;

    /** The court / authority that delivered the judgment */
    @Column(nullable = false)
    private String judgingCourt;

    /** Date the judgment was delivered */
    @Column(nullable = false)
    private LocalDate judgmentDate;

    /** The type of judgment outcome */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppealJudgment judgment;

    /** Detailed summary of the judgment and its implications */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String judgmentSummary;

    /**
     * If judgment is REMITTED, describes what the lower court/authority
     * must reconsider.
     */
    @Column(columnDefinition = "TEXT")
    private String remittalInstructions;

    /** Reference number of the judgment (e.g. CA/2026/0078) */
    @Column
    private String judgmentReference;

    /** Additional notes */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;
}
