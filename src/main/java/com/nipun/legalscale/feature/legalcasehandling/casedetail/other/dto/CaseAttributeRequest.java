package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.AttributeDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CaseAttributeRequest {

    /**
     * Machine-readable attribute key — must be unique within the case.
     * Use camelCase, e.g. "respondentNIC", "contractValue", "injunctionDate".
     */
    @NotBlank(message = "Attribute name (key) is required")
    private String attributeName;

    /** Human-readable label for display in the UI */
    @NotBlank(message = "Display label is required")
    private String displayLabel;

    @NotBlank(message = "Attribute value is required")
    private String attributeValue;

    @NotNull(message = "Data type is required")
    private AttributeDataType dataType;

    /** Optional grouping category (e.g. "Party Details", "Financial", "Dates") */
    private String category;

    /** Sort order within the category group — lower values appear first */
    private Integer displayOrder;

    private boolean required;

    private String notes;
}
