package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.OwnershipType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OwnershipRecordResponse {

    private Long id;
    private Long caseId;
    private String ownerName;
    private String ownerIdentificationNumber;
    private String ownerAddress;
    private OwnershipType ownershipType;
    private LocalDate ownershipStartDate;

    /** Null if current owner */
    private LocalDate ownershipEndDate;

    /** True when ownershipEndDate is null */
    private boolean currentOwner;

    private String deedReference;
    private String notes;
    private String recordedByName;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
}
