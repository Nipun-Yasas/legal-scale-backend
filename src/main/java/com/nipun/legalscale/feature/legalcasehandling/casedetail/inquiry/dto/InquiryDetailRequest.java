package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InquiryDetailRequest {

    @NotBlank(message = "Inquiry subject is required")
    private String inquirySubject;

    @NotBlank(message = "Commissioned by is required")
    private String commissionedBy;

    @NotNull(message = "Commissioned date is required")
    private LocalDate commissionedDate;

    @NotBlank(message = "Terms of reference are required")
    private String termsOfReference;

    private LocalDate reportingDeadline;
    private LocalDate reportSubmittedDate;
    private String notes;
}
