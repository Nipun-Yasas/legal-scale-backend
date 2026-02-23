package com.nipun.legalscale.feature.agreementapproval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignAgreementRequest {
    @NotBlank(message = "Cryptographic key is required")
    private String cryptographicKey;
}
