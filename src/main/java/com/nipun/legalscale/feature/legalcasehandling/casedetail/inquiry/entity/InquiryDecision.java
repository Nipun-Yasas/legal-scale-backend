package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.DecisionStatus;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A management decision made in response to one or more inquiry findings.
 * Decisions are tracked through a lifecycle: PENDING → IN_PROGRESS →
 * IMPLEMENTED.
 *
 * Feature 3 – Decision Tracking.
 */
@Entity
@Table(name = "inquiry_decisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_detail_id", nullable = false)
    private InquiryDetail inquiryDetail;

    /**
     * Optional reference to the specific finding this decision addresses.
     * Null if the decision applies to the inquiry as a whole.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finding_id")
    private InquiryFinding relatedFinding;

    /** Short title of the decision */
    @Column(nullable = false)
    private String decisionTitle;

    /** Full details of what management has decided */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String decisionDetails;

    /**
     * The officer, unit, or department responsible for carrying out this decision
     */
    @Column(nullable = false)
    private String responsibleParty;

    /** Target date for implementation */
    @Column
    private LocalDate targetDate;

    /** Actual date of implementation */
    @Column
    private LocalDate implementedDate;

    /** Current implementation status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DecisionStatus status = DecisionStatus.PENDING;

    /** Notes on progress, reasons for deferral/rejection, etc. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit ──────────────────────────────────────────────────────────────────

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
