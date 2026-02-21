package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.OwnershipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OwnershipRecordRequest {

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    /** NIC, passport, or company registration number */
    private String ownerIdentificationNumber;

    private String ownerAddress;

    @NotNull(message = "Ownership type is required")
    private OwnershipType ownershipType;

    @NotNull(message = "Ownership start date is required")
    private LocalDate ownershipStartDate;

    /**
     * Leave null if this is the current / ongoing owner.
     * Must be after ownershipStartDate.
     */
    private LocalDate ownershipEndDate;

    /** Reference to the deed that evidences this transfer */
    private String deedReference;

    private String notes;
}
