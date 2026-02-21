package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single deadline associated with an appeal (e.g. hearing date, filing
 * deadline).
 * Multiple deadlines can exist per AppealDetail.
 *
 * Feature 2 – Appeal Deadline Tracking.
 */
@Entity
@Table(name = "appeal_deadlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealDeadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_detail_id", nullable = false)
    private AppealDetail appealDetail;

    /** What this deadline is for */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeadlineType deadlineType;

    /** The actual deadline / scheduled date */
    @Column(nullable = false)
    private LocalDate deadlineDate;

    /** Current status of this deadline */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeadlineStatus status = DeadlineStatus.PENDING;

    /**
     * If status is EXTENDED, this is the new extended deadline date.
     * Null for non-extended deadlines.
     */
    private LocalDate extendedDeadlineDate;

    /** Description or court order reference */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_updated_by_id")
    private UserEntity statusUpdatedBy;

    private LocalDateTime statusUpdatedAt;
}
