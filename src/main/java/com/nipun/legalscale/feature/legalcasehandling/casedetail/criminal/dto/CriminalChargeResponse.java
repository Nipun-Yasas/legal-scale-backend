package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.ChargeStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.PleaType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CriminalChargeResponse {

    private Long id;
    private Long caseId;
    private String statute;
    private String section;
    private String offenceName;
    private String offenceDescription;
    private String maximumPenalty;
    private PleaType plea;
    private ChargeStatus status;
    private String outcomeDetails;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
