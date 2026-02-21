package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity;

import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A custom document template associated with an OTHER-type case.
 *
 * Supports two complementary modes:
 * 1. Inline content: the template body (Markdown, HTML, or plain text with
 * placeholders like {{caseTitle}}, {{referenceNumber}}) is stored in
 * {@code templateContent}.
 * 2. File-based: a template file uploaded via the document service is linked
 * via {@code uploadedTemplate}.
 *
 * Both modes can coexist. Version numbering is maintained so that prior
 * versions
 * are not lost when a template is updated (create new, archive old).
 *
 * Feature 2 – Custom Document Templates.
 */
@Entity
@Table(name = "other_case_document_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseDocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_case_detail_id", nullable = false)
    private OtherCaseDetail otherCaseDetail;

    /**
     * Short name identifying this template (e.g. "Notice to Respondent", "Demand
     * Letter")
     */
    @Column(nullable = false)
    private String templateName;

    /** Describes the purpose of this template and when it should be used */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Inline template body.
     * May contain placeholder tokens such as {{caseTitle}}, {{referenceNumber}},
     * {{officerName}} which the UI substitutes at generation time.
     * Null if template is file-based only.
     */
    @Column(columnDefinition = "TEXT")
    private String templateContent;

    /**
     * Version label, e.g. "v1.0", "v2.1".
     * New versions should be created as separate records (the old one ARCHIVED).
     */
    @Column(nullable = false)
    @Builder.Default
    private String version = "v1.0";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TemplateStatus status = TemplateStatus.DRAFT;

    /**
     * Optional link to a document file uploaded via the document service.
     * Null if this is an inline-only template.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_template_document_id")
    private Document uploadedTemplate;

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
