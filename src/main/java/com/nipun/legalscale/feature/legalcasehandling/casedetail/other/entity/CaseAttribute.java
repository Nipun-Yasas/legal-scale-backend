package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.AttributeDataType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single configurable attribute on an OTHER-type case.
 *
 * Implements an Entity-Attribute-Value (EAV) pattern so that arbitrary,
 * case-specific fields can be added without schema changes.
 *
 * Each attribute has:
 * - a name (key) e.g. "respondentNIC", "contractValue", "injunctionDate"
 * - a value (always stored as text)
 * - a dataType hint so the UI can render / validate correctly
 * - an optional category to group related attributes together
 * - a displayOrder to control rendering sequence
 *
 * Feature 1 – Configurable Case Attributes.
 */
@Entity
@Table(name = "other_case_attributes", uniqueConstraints = @UniqueConstraint(columnNames = { "other_case_detail_id",
        "attribute_name" }, name = "uq_case_attribute_name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_case_detail_id", nullable = false)
    private OtherCaseDetail otherCaseDetail;

    /** The attribute key, unique within a case (e.g. "respondentNIC") */
    @Column(name = "attribute_name", nullable = false)
    private String attributeName;

    /** Human-readable label shown in the UI (e.g. "Respondent NIC Number") */
    @Column(nullable = false)
    private String displayLabel;

    /** The attribute value, always stored as a string */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String attributeValue;

    /** The data type to use when interpreting / rendering the value */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttributeDataType dataType = AttributeDataType.TEXT;

    /**
     * Optional grouping category (e.g. "Party Details", "Financial", "Dates").
     * Allows the UI to render attributes in grouped sections.
     */
    @Column
    private String category;

    /**
     * Optional sort order within its category.
     * Lower values sort first.
     */
    @Column
    @Builder.Default
    private Integer displayOrder = 0;

    /** Whether this attribute is mandatory for this case type */
    @Column(nullable = false)
    @Builder.Default
    private boolean required = false;

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
}
