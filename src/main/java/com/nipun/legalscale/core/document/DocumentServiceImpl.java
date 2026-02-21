package com.nipun.legalscale.core.document;

import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${app.document.upload-dir:/tmp/legalscale-uploads}")
    private String uploadDir;

    /**
     * Runs once at startup — logs the active upload directory.
     * This makes it easy to confirm the path in Render's deployment logs.
     */
    @PostConstruct
    public void init() {
        log.info("Document upload directory: {}", Paths.get(uploadDir).toAbsolutePath());
    }

    @Override
    public DocumentResponse upload(MultipartFile file, Long uploadedByUserId) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            Path filePath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Document document = Document.builder()
                    .fileName(originalFileName)
                    .fileType(file.getContentType())
                    .fileUrl(filePath.toString())
                    .uploadDate(LocalDateTime.now())
                    .uploadedByUserId(uploadedByUserId)
                    .build();

            Document saved = documentRepository.save(document);
            return toResponse(saved);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentResponse findById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
        return toResponse(document);
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getFileType(),
                document.getFileUrl(),
                document.getUploadDate(),
                document.getUploadedByUserId());
    }
}
