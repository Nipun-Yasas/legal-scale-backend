package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for an OTHER-type case (1:1 with InitialCaseEntity).
 *
 * Acts as the anchor for both configurable attributes and custom document
 * templates.
 * The {@code caseNature} field captures a free-text label for what kind of
 * unique
 * legal matter this is (e.g., "Contempt of Court", "Injunction Application").
 */
@Entity
@Table(name = "other_case_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherCaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    /**
     * A short label describing the nature of this unique legal matter.
     * E.g. "Contempt of Court", "Injunction Application", "Extradition Request".
     */
    @Column(nullable = false)
    private String caseNature;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ─── Audit ───────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private UserEntity lastUpdatedBy;

    private LocalDateTime lastUpdatedAt;

    // ─── Feature 1: Configurable Attributes ──────────────────────────────────────

    @OneToMany(mappedBy = "otherCaseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseAttribute> attributes = new ArrayList<>();

    // ─── Feature 2: Document Templates ───────────────────────────────────────────

    @OneToMany(mappedBy = "otherCaseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseDocumentTemplate> templates = new ArrayList<>();
}
