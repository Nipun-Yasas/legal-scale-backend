package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.FindingSeverity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single finding made by the inquiry panel.
 * Findings are numbered sequentially within a case (findingNumber = 1, 2, 3
 * ...).
 * Each finding carries a severity level and a recommendation for action.
 *
 * Feature 2 – Findings & Recommendations.
 */
@Entity
@Table(name = "inquiry_findings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_detail_id", nullable = false)
    private InquiryDetail inquiryDetail;

    /** Sequential finding number within this inquiry (e.g. 1, 2, 3) */
    @Column(nullable = false)
    private Integer findingNumber;

    /** Short title or heading for this finding */
    @Column(nullable = false)
    private String findingTitle;

    /** Full description of what the panel found */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String findingDescription;

    /** Severity level of this finding */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FindingSeverity severity;

    /**
     * The panel's recommendation arising from this finding.
     * E.g. "Dismiss the officer", "Institute disciplinary proceedings",
     * "Improve procurement controls".
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;

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
