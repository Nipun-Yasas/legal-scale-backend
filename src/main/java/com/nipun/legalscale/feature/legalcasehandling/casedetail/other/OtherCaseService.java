package com.nipun.legalscale.feature.legalcasehandling.casedetail.other;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto.*;

public interface OtherCaseService {

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    /**
     * Set or update the case nature label and description. Creates on first call.
     */
    OtherCaseDetailResponse setCaseDetails(Long caseId, OtherCaseDetailRequest request);

    /** Get the full OTHER case summary. */
    OtherCaseDetailResponse getCaseDetail(Long caseId);

    // ─── Feature 1: Configurable Attributes ──────────────────────────────────────

    /**
     * Add a configurable attribute. Attribute name must be unique within the case.
     */
    CaseAttributeResponse addAttribute(Long caseId, CaseAttributeRequest request);

    /** Update an existing attribute (key, label, value, type, category, order). */
    CaseAttributeResponse updateAttribute(Long caseId, Long attributeId, CaseAttributeRequest request);

    /** Remove an attribute. */
    void deleteAttribute(Long caseId, Long attributeId);

    // ─── Feature 2: Document Templates ───────────────────────────────────────────

    /** Add a new document template (inline content and/or uploaded file). */
    CaseDocumentTemplateResponse addTemplate(Long caseId, CaseDocumentTemplateRequest request);

    /** Update template content (produces an updated template in-place). */
    CaseDocumentTemplateResponse updateTemplate(Long caseId, Long templateId, CaseDocumentTemplateRequest request);

    /** Change the lifecycle status of a template (DRAFT → ACTIVE → ARCHIVED). */
    CaseDocumentTemplateResponse updateTemplateStatus(Long caseId, Long templateId,
            TemplateStatusUpdateRequest request);

    /** Remove a template. */
    void deleteTemplate(Long caseId, Long templateId);
}
