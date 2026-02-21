package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CaseDocumentTemplateResponse {

    private Long id;
    private Long caseId;
    private String templateName;
    private String description;
    private String templateContent;
    private String version;
    private TemplateStatus status;
    private String notes;
    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;

    // ─── Uploaded file info (null if inline-only) ──────────────────────────────
    private Long uploadedTemplateDocumentId;
    private String uploadedTemplateFileName;
    private String uploadedTemplateUrl;
}
