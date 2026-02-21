package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingOutcome;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single court proceeding / hearing in the case history.
 * Multiple hearings exist per CriminalCaseDetail, building the full timeline.
 *
 * Feature 2 – Hearing History.
 */
@Entity
@Table(name = "court_hearings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtHearing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criminal_case_detail_id", nullable = false)
    private CriminalCaseDetail criminalCaseDetail;

    /** Date this hearing was held */
    @Column(nullable = false)
    private LocalDate hearingDate;

    /** Type of proceeding held on this date */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingType hearingType;

    /** Name of the presiding judge on this date */
    @Column
    private String presidingJudge;

    /** Summary of what transpired at this hearing */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String proceedingsSummary;

    /** Outcome of the hearing */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingOutcome outcome;

    /**
     * If outcome is ADJOURNED, the date to which the hearing was adjourned.
     * Null for other outcomes.
     */
    @Column
    private LocalDate nextHearingDate;

    /**
     * Purpose/description of the next hearing date.
     * E.g. "Witness examination to continue", "Submissions on sentencing".
     */
    @Column
    private String nextHearingPurpose;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit ───────────────────────────────────────────────────────────────────

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
