package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity;

import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.LandDocumentType;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registry entry for a deed, survey plan, or related land document.
 *
 * Two complementary approaches are supported:
 * 1. Registry-only: record the document metadata (reference, issuing authority,
 * issue date) without a file upload. Useful for physical documents.
 * 2. Uploaded: link to a {@link Document} entity for documents uploaded via the
 * document service. Both can be set at the same time.
 *
 * Feature 3 – Deed & Plan Management.
 */
@Entity
@Table(name = "land_deeds_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDeedPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_detail_id", nullable = false)
    private LandDetail landDetail;

    /** What type of document this is */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LandDocumentType documentType;

    /** Official reference / folio / file number on the document */
    @Column(nullable = false)
    private String documentReference;

    /** Date the document was issued */
    @Column
    private LocalDate issueDate;

    /**
     * Office/authority that issued this document (e.g. "Survey Department", "Land
     * Registry - Colombo")
     */
    @Column
    private String issuingAuthority;

    /**
     * Optional link to the digital copy uploaded through the document service.
     * Null if only the registry entry is being recorded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document uploadedDocument;

    /** Additional notes */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserEntity recordedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
