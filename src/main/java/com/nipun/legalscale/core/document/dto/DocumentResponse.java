package com.nipun.legalscale.core.document.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String fileName,
        String fileType,
        String fileUrl,
        LocalDateTime uploadDate,
        Long uploadedByUserId
) {}
