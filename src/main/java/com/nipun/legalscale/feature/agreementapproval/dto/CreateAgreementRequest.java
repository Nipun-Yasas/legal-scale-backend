package com.nipun.legalscale.feature.agreementapproval.dto;

import com.nipun.legalscale.feature.agreementapproval.enums.AgreementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateAgreementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private AgreementType type;

    @NotBlank(message = "Parties involved are required")
    private String parties;

    private BigDecimal value;

    private LocalDate startDate;
    private LocalDate endDate;

    private Long linkedCaseId;

    private Long reviewerId;
}
