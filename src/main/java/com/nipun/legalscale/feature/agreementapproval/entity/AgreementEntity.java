package com.nipun.legalscale.feature.agreementapproval.entity;

import com.nipun.legalscale.feature.agreementapproval.enums.AgreementStatus;
import com.nipun.legalscale.feature.agreementapproval.enums.AgreementType;
import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementType type;

    @Column(nullable = false)
    private String parties;

    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AgreementStatus status = AgreementStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private UserEntity reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private UserEntity approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_case_id")
    private InitialCaseEntity linkedCase;

    @Column(columnDefinition = "TEXT")
    private String approvalRemarks;

    @Builder.Default
    private Boolean isDigitallySigned = false;

    @Builder.Default
    private Boolean renewalAlertSent = false;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgreementVersionEntity> versions = new ArrayList<>();

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgreementCommentEntity> comments = new ArrayList<>();
}
