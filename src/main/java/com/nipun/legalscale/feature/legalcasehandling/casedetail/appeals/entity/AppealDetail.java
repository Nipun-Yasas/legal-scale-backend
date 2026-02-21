package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for an APPEALS case (1:1 with InitialCaseEntity).
 *
 * Feature 1 – Original Case Linking:
 * The appeal is optionally linked to the original InitialCaseEntity it
 * challenges
 * via the {@code originalCase} field. This is a soft reference — the original
 * case may be in the same system or described only by reference number/court.
 *
 * Owns the appeal deadlines and the optional outcome record.
 */
@Entity
@Table(name = "appeal_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The APPEALS case this detail belongs to */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    // ─── Feature 1: Original Case Linking ────────────────────────────────────────

    /**
     * Link to the original case within this system that is being appealed.
     * Nullable — if the original case is from an external court or another
     * system, use {@code originalCaseReference} instead.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_case_id")
    private InitialCaseEntity originalCase;

    /**
     * Free-text reference to the original case when it is external to this system
     * (e.g., "DC/COL/2024/1234" or "High Court Case No. HC/117/2023").
     */
    @Column
    private String originalCaseReference;

    /** The court / authority where the appeal is being heard */
    @Column(nullable = false)
    private String appealCourt;

    /** Date the appeal was formally filed */
    @Column(nullable = false)
    private LocalDate filingDate;

    /** Grounds / basis of the appeal */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String groundsOfAppeal;

    /** Additional notes */
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit
    // ────────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;

    // ─── Feature 2: Appeal Deadlines ─────────────────────────────────────────────

    @OneToMany(mappedBy = "appealDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AppealDeadline> deadlines = new ArrayList<>();

    // ─── Feature 3: Appeal Outcome
    // ────────────────────────────────────────────────

    /** Null until a judgment / outcome is recorded */
    @OneToOne(mappedBy = "appealDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppealOutcome outcome;
}
