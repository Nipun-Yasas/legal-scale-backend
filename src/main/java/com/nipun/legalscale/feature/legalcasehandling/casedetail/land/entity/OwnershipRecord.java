package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.OwnershipType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single ownership record in the ownership history of a land parcel.
 * Multiple records exist per LandDetail — together they form the full ownership
 * chain.
 *
 * A null {@code ownershipEndDate} means this is the current owner.
 *
 * Feature 2 – Ownership History Tracking.
 */
@Entity
@Table(name = "land_ownership_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnershipRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_detail_id", nullable = false)
    private LandDetail landDetail;

    /** Full name of the owner during this ownership period */
    @Column(nullable = false)
    private String ownerName;

    /** NIC, passport number, or company registration number of the owner */
    @Column
    private String ownerIdentificationNumber;

    /** Contact address of the owner */
    @Column(columnDefinition = "TEXT")
    private String ownerAddress;

    /** Type of ownership held during this period */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnershipType ownershipType;

    /** Date ownership commenced (transfer date, grant date, etc.) */
    @Column(nullable = false)
    private LocalDate ownershipStartDate;

    /**
     * Date ownership ended. Null if this is the current owner.
     */
    @Column
    private LocalDate ownershipEndDate;

    /** Reference to the deed that evidences this transfer of ownership */
    @Column
    private String deedReference;

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
