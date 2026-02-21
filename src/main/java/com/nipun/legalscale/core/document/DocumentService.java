package com.nipun.legalscale.core.document;

import com.nipun.legalscale.core.document.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentResponse upload(MultipartFile file, Long uploadedByUserId);
    DocumentResponse findById(Long id);
}
