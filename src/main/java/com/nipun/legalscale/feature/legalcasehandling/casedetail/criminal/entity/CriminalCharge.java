package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.ChargeStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.PleaType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single criminal charge levelled against the accused in a case.
 * Multiple charges can exist per CriminalCaseDetail.
 *
 * Feature 1 – Charge Management.
 */
@Entity
@Table(name = "criminal_charges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriminalCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criminal_case_detail_id", nullable = false)
    private CriminalCaseDetail criminalCaseDetail;

    // ─── Statute & Offence Details ───────────────────────────────────────────────

    /**
     * Name of the Act / statute (e.g. "Penal Code", "Prevention of Terrorism Act")
     */
    @Column(nullable = false)
    private String statute;

    /** Section number within the statute (e.g. "296", "300(a)") */
    @Column(nullable = false)
    private String section;

    /** Human-readable name of the offence */
    @Column(nullable = false)
    private String offenceName;

    /** Detailed description of how the offence was committed */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String offenceDescription;

    /** Maximum sentence / penalty for this offence as per the statute */
    @Column
    private String maximumPenalty;

    // ─── Plea & Outcome ──────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PleaType plea = PleaType.NO_PLEA_ENTERED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChargeStatus status = ChargeStatus.PENDING;

    /**
     * Sentence imposed if convicted, or reason if withdrawn.
     * E.g. "5 years RI" or "Insufficient evidence".
     */
    @Column(columnDefinition = "TEXT")
    private String outcomeDetails;

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
