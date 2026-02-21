package com.nipun.legalscale.feature.agreementapproval.dto;

import com.nipun.legalscale.feature.agreementapproval.enums.AgreementStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewAgreementRequest {
    @NotNull(message = "Review status must be provided")
    private AgreementStatus reviewStatus;

    private String remarks; // used for comments or rejection/approval remarks
}
