package com.nipun.legalscale.core.document;

import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final S3Client s3Client;

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        log.info("S3 Document Service initialized for bucket: {}", bucketName);
    }

    @Override
    public DocumentResponse upload(MultipartFile file, Long uploadedByUserId) {
        try {
            String origName = file.getOriginalFilename();
            String originalFileName = (origName != null && !origName.isBlank()) ? origName : "unnamed_file";

            String contentType = file.getContentType();
            String fileType = (contentType != null && !contentType.isBlank()) ? contentType
                    : "application/octet-stream";

            String storedFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_"); // Replace
                                                                                                                  // special
                                                                                                                  // characters
                                                                                                                  // that
                                                                                                                  // might
                                                                                                                  // cause
                                                                                                                  // issues

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .contentType(fileType)
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

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
            throw new RuntimeException("Failed to read file input stream: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file in S3: " + e.getMessage(), e);
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
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(document.getFileUrl())
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(s3Object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3: " + e.getMessage(), e);
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
