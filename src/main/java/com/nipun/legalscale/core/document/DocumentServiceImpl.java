package com.nipun.legalscale.core.document;

import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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

    private final String uploadDir = "uploads";

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Local document upload directory initialized at: {}", uploadDir);
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
        }
    }

    @Override
    public DocumentResponse upload(MultipartFile file, Long uploadedByUserId) {
        try {
            String origName = file.getOriginalFilename();
            String originalFileName = (origName != null && !origName.isBlank()) ? origName : "unnamed_file";

            String contentType = file.getContentType();
            String fileType = (contentType != null && !contentType.isBlank()) ? contentType
                    : "application/octet-stream";

            String storedFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");

            Path targetLocation = Paths.get(uploadDir).resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Document document = Document.builder()
                    .fileName(originalFileName)
                    .fileType(fileType)
                    .fileUrl(storedFileName)
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

    @Override
    public Resource download(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));

        try {
            Path filePath = Paths.get(uploadDir).resolve(document.getFileUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + document.getFileUrl());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not download file: " + e.getMessage(), e);
        }
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
