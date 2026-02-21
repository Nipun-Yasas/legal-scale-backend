package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for a CRIMINAL case (1:1 with InitialCaseEntity).
 * Captures accused information, court details, and case file reference.
 * Owns the charge list and hearing history.
 */
@Entity
@Table(name = "criminal_case_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriminalCaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    // ─── Accused Info ───────────────────────────────────────────────────────────

    @Column(nullable = false)
    private String accusedName;

    /** NIC, passport number, or other ID */
    @Column
    private String accusedIdNumber;

    @Column(columnDefinition = "TEXT")
    private String accusedAddress;

    // ─── Court & Case File Info ──────────────────────────────────────────────────

    /** Court handling this matter */
    @Column(nullable = false)
    private String court;

    /** Court case file / cause number (e.g. "HC/COL/0045/2024") */
    @Column
    private String courtCaseNumber;

    /** Name of the presiding judge (may change hearing to hearing) */
    @Column
    private String presidingJudge;

    /** Date the case was first filed in court */
    @Column
    private LocalDate courtFilingDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit ──────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;

    // ─── Feature 1: Charges ──────────────────────────────────────────────────────

    @OneToMany(mappedBy = "criminalCaseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CriminalCharge> charges = new ArrayList<>();

    // ─── Feature 2: Hearings ─────────────────────────────────────────────────────

    @OneToMany(mappedBy = "criminalCaseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CourtHearing> hearings = new ArrayList<>();
}
