package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Used to set or update the land reference and survey details.
 */
@Data
public class LandDetailRequest {

    @NotBlank(message = "Land reference number is required")
    private String landReferenceNumber;

    private String surveyPlanNumber;
    private String lotNumber;
    private String planNumber;

    private BigDecimal extent;

    /** Unit for the extent value: "perches", "acres", "hectares", "sq.m" etc. */
    private String extentUnit;

    private String province;
    private String district;
    private String dsDivision;
    private String gnDivision;
    private String address;
    private String landRegistryDivision;
    private String notes;
}
