package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.PanelMemberRole;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single member of the inquiry panel.
 * Multiple members exist per InquiryDetail.
 *
 * Feature 1 – Inquiry Panel Setup.
 */
@Entity
@Table(name = "inquiry_panel_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_detail_id", nullable = false)
    private InquiryDetail inquiryDetail;

    /** Full name of the panel member */
    @Column(nullable = false)
    private String memberName;

    /** Job title / designation (e.g. "Senior Deputy Solicitor General") */
    @Column(nullable = false)
    private String designation;

    /** Department or organisation the member represents */
    @Column
    private String department;

    /** Role held within the panel */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PanelMemberRole role;

    /** Date the member was formally appointed to the panel */
    @Column
    private LocalDate appointedDate;

    /** Contact details for official correspondence */
    @Column
    private String contactDetails;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
