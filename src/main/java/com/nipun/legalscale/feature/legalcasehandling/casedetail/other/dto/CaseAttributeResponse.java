package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.AttributeDataType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CaseAttributeResponse {

    private Long id;
    private Long caseId;
    private String attributeName;
    private String displayLabel;
    private String attributeValue;
    private AttributeDataType dataType;
    private String category;
    private Integer displayOrder;
    private boolean required;
    private String notes;
    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
