package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaseDocumentTemplateRequest {

    @NotBlank(message = "Template name is required")
    private String templateName;

    @NotBlank(message = "Template description is required")
    private String description;

    /**
     * Inline template body. May contain placeholder tokens such as:
     * {{caseTitle}}, {{referenceNumber}}, {{officerName}}, {{currentDate}}
     * Leave null if template is provided only as an uploaded file.
     */
    private String templateContent;

    /** Version label, e.g. "v1.0", "v2.1". Defaults to "v1.0". */
    private String version;

    /**
     * ID of an already-uploaded Document to use as the template file.
     * Leave null for inline-content templates.
     */
    private Long uploadedTemplateDocumentId;

    private String notes;
}
