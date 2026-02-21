package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for a LAND case (1:1 with InitialCaseEntity).
 *
 * Feature 1 – Land Reference Management:
 * Stores the authoritative land identification data: reference number,
 * survey plan number, lot/plan numbers, extent, location, and registry
 * division.
 *
 * Owns the ownership history records and the deed/plan registry.
 */
@Entity
@Table(name = "land_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    // ─── Feature 1: Land Reference Management ────────────────────────────────────

    /** Official land reference / assessment number (e.g. LRI-2024-00441) */
    @Column(nullable = false)
    private String landReferenceNumber;

    /** Survey plan number from the Survey Department */
    @Column
    private String surveyPlanNumber;

    /** Lot number as shown on the survey plan */
    @Column
    private String lotNumber;

    /** Plan number from the Survey Department */
    @Column
    private String planNumber;

    /**
     * Land extent / area.
     * Stored as a decimal; use the {@code extentUnit} for the unit of measure.
     */
    @Column(precision = 15, scale = 4)
    private BigDecimal extent;

    /** Unit for the extent value (e.g. "perches", "acres", "hectares", "sq.m") */
    @Column
    private String extentUnit;

    /** Province where the land is located */
    @Column
    private String province;

    /** District */
    @Column
    private String district;

    /** Local government area / DS division */
    @Column
    private String dsDivision;

    /** GN (Grama Niladhari) division */
    @Column
    private String gnDivision;

    /** Street / village address */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Land Registry office responsible for this land */
    @Column
    private String landRegistryDivision;

    /** Additional notes or remarks about the land reference */
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

    // ─── Feature 2: Ownership History ────────────────────────────────────────────

    @OneToMany(mappedBy = "landDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OwnershipRecord> ownershipHistory = new ArrayList<>();

    // ─── Feature 3: Deeds & Plans ────────────────────────────────────────────────

    @OneToMany(mappedBy = "landDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LandDeedPlan> deedsPlansList = new ArrayList<>();
}
