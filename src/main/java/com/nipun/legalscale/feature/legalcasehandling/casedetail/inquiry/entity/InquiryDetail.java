package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Header record for an INQUIRIES case (1:1 with InitialCaseEntity).
 *
 * Captures the inquiry mandate — what was investigated, who commissioned it,
 * the terms of reference, and the reporting timeline.
 *
 * Owns the panel members, findings, and management decisions.
 */
@Entity
@Table(name = "inquiry_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private InitialCaseEntity initialCase;

    // ─── Inquiry Mandate ─────────────────────────────────────────────────────────

    /** Brief subject / title of the inquiry */
    @Column(nullable = false)
    private String inquirySubject;

    /** The authority or officer who commissioned this inquiry */
    @Column(nullable = false)
    private String commissionedBy;

    /** Date the inquiry was formally commissioned / authorised */
    @Column(nullable = false)
    private LocalDate commissionedDate;

    /** Full terms of reference given to the inquiry panel */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String termsOfReference;

    /** Deadline by which the panel must report */
    @Column
    private LocalDate reportingDeadline;

    /** Actual date the panel submitted its final report */
    @Column
    private LocalDate reportSubmittedDate;

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

    // ─── Feature 1: Panel Members ────────────────────────────────────────────────

    @OneToMany(mappedBy = "inquiryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PanelMember> panelMembers = new ArrayList<>();

    // ─── Feature 2: Findings ─────────────────────────────────────────────────────

    @OneToMany(mappedBy = "inquiryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InquiryFinding> findings = new ArrayList<>();

    // ─── Feature 3: Decisions ────────────────────────────────────────────────────

    @OneToMany(mappedBy = "inquiryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InquiryDecision> decisions = new ArrayList<>();
}
