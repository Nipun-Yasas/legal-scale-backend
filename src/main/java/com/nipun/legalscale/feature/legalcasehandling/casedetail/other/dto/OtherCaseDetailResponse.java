package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Complete OTHER case summary covering both features.
 */
@Data
@Builder
public class OtherCaseDetailResponse {

    private Long id;
    private Long caseId;
    private String caseTitle;
    private String referenceNumber;

    // ─── Header ──────────────────────────────────────────────────────────────────

    private String caseNature;
    private String description;
    private String notes;

    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;

    private String lastUpdatedByName;
    private String lastUpdatedByEmail;
    private LocalDateTime lastUpdatedAt;

    // ─── Feature 1: Configurable Attributes ──────────────────────────────────────

    /** Flat list of all attributes, ordered by category → displayOrder → name */
    private List<CaseAttributeResponse> attributes;

    /**
     * Attributes grouped by their category label.
     * Key = category name (or "Uncategorised" when category is null).
     * Useful for rendering grouped forms in the UI.
     */
    private Map<String, List<CaseAttributeResponse>> attributesByCategory;

    private int totalAttributes;

    // ─── Feature 2: Document Templates ───────────────────────────────────────────

    private List<CaseDocumentTemplateResponse> templates;
    private long activeTemplates;
    private long draftTemplates;
    private long archivedTemplates;
}
