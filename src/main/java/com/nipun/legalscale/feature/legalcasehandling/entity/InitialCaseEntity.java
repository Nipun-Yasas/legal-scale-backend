package com.nipun.legalscale.feature.legalcasehandling.entity;

import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "initial_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialCaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Core Fields ────────────────────────────────────────────────────────────

    @Column(nullable = false)
    private String caseTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseType caseType;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private String partiesInvolved;

    @Column(nullable = false)
    private String natureOfCase;

    @Column(nullable = false)
    private LocalDate dateOfOccurrenceOrFiling;

    @Column(nullable = false)
    private String courtOrAuthority;

    @Column(precision = 15, scale = 2)
    private BigDecimal financialExposure;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summaryOfFacts;

    // ─── Status ──────────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CaseStatus status = CaseStatus.NEW;

    // ─── Ownership & Assignment
    // ───────────────────────────────────────────────────

    /** The supervisor who created this case */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdSupervisor;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** The officer this case was assigned to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private UserEntity assignedOfficer;

    private LocalDateTime assignedAt;

    // ─── Approval Audit
    // ───────────────────────────────────────────────────────────

    /** Who approved this case (officer or supervisor) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private UserEntity approvedBy;

    private LocalDateTime approvedAt;

    // ─── Closure Audit
    // ────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id")
    private UserEntity closedBy;

    private LocalDateTime closedAt;

    @Column(columnDefinition = "TEXT")
    private String closingRemarks;

    // ─── Comments
    // ─────────────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "initialCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseCommentEntity> comments = new ArrayList<>();

    // ─── Supporting Attachments
    // ───────────────────────────────────────────────────

    /**
     * Documents uploaded as supporting attachments for this case.
     * Uses a join table so the Document entity stays generic.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "case_attachments", joinColumns = @JoinColumn(name = "case_id"), inverseJoinColumns = @JoinColumn(name = "document_id"))
    @Builder.Default
    private List<Document> supportingAttachments = new ArrayList<>();
}
