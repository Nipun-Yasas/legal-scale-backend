package com.nipun.legalscale.feature.legalcasehandling.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {

    @NotBlank(message = "Comment text is required")
    private String comment;
}
