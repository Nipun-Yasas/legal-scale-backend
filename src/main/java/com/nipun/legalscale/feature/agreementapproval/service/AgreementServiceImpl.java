package com.nipun.legalscale.feature.agreementapproval.service;

import com.nipun.legalscale.core.document.DocumentService;
import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import com.nipun.legalscale.feature.agreementapproval.dto.*;
import com.nipun.legalscale.feature.agreementapproval.entity.AgreementCommentEntity;
import com.nipun.legalscale.feature.agreementapproval.entity.AgreementEntity;
import com.nipun.legalscale.feature.agreementapproval.entity.AgreementVersionEntity;
import com.nipun.legalscale.feature.agreementapproval.enums.AgreementStatus;
import com.nipun.legalscale.feature.agreementapproval.repository.AgreementCommentRepository;
import com.nipun.legalscale.feature.agreementapproval.repository.AgreementRepository;
import com.nipun.legalscale.feature.agreementapproval.repository.AgreementVersionRepository;
import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.legalcasehandling.repository.InitialCaseRepository;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {

    private final AgreementRepository agreementRepository;
    private final AgreementVersionRepository versionRepository;
    private final AgreementCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final InitialCaseRepository initialCaseRepository;

    private UserEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private AgreementResponse toResponse(AgreementEntity entity) {
        List<AgreementVersionResponse> versions = entity.getVersions().stream()
                .map(v -> AgreementVersionResponse.builder()
                        .id(v.getId())
                        .agreementId(entity.getId())
                        .versionNumber(v.getVersionNumber())
                        .documentId(v.getDocument() != null ? v.getDocument().getId() : null)
                        .documentName(v.getDocument() != null ? v.getDocument().getFileName() : null)
                        .documentUrl(v.getDocument() != null ? v.getDocument().getFileUrl() : null)
                        .uploadedAt(v.getUploadedAt())
                        .uploadedById(v.getUploadedBy().getId())
                        .uploadedByName(v.getUploadedBy().getFullName())
                        .versionNotes(v.getVersionNotes())
                        .build())
                .collect(Collectors.toList());

        List<AgreementCommentResponse> comments = entity.getComments().stream()
                .map(c -> AgreementCommentResponse.builder()
                        .id(c.getId())
                        .agreementId(entity.getId())
                        .commentedById(c.getCommentedBy().getId())
                        .commentedByName(c.getCommentedBy().getFullName())
                        .commentText(c.getCommentText())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return AgreementResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .parties(entity.getParties())
                .value(entity.getValue())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdById(entity.getCreatedBy().getId())
                .createdByName(entity.getCreatedBy().getFullName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .reviewerId(entity.getReviewer() != null ? entity.getReviewer().getId() : null)
                .reviewerName(entity.getReviewer() != null ? entity.getReviewer().getFullName() : null)
                .approverId(entity.getApprover() != null ? entity.getApprover().getId() : null)
                .approverName(entity.getApprover() != null ? entity.getApprover().getFullName() : null)
                .linkedCaseId(entity.getLinkedCase() != null ? entity.getLinkedCase().getId() : null)
                .approvalRemarks(entity.getApprovalRemarks())
                .isDigitallySigned(entity.getIsDigitallySigned())
                .versions(versions)
                .comments(comments)
                .build();
    }

    @Override
    @Transactional
    public AgreementResponse createAgreement(CreateAgreementRequest request, MultipartFile documentFile) {
        UserEntity creator = currentUser();

        AgreementEntity agreement = AgreementEntity.builder()
                .title(request.getTitle())
                .type(request.getType())
                .parties(request.getParties())
                .value(request.getValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(AgreementStatus.DRAFT)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();

        if (request.getLinkedCaseId() != null) {
            InitialCaseEntity linkedCase = initialCaseRepository.findById(request.getLinkedCaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Case not found"));
            agreement.setLinkedCase(linkedCase);
        }

        if (request.getReviewerId() != null) {
            UserEntity reviewer = userRepository.findById(request.getReviewerId())
                    .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
            agreement.setReviewer(reviewer);
        }

        agreement = agreementRepository.save(agreement);

        if (documentFile != null && !documentFile.isEmpty()) {
            DocumentResponse uploaded = documentService.upload(documentFile, creator.getId());
            Document docEntity = documentRepository.findById(uploaded.id())
                    .orElseThrow(() -> new IllegalStateException("Document not found"));

            AgreementVersionEntity version = AgreementVersionEntity.builder()
                    .agreement(agreement)
                    .versionNumber(1)
                    .document(docEntity)
                    .uploadedAt(LocalDateTime.now())
                    .uploadedBy(creator)
                    .versionNotes("Initial draft")
                    .build();

            versionRepository.save(version);
            agreement.getVersions().add(version);
        }

        return toResponse(agreement);
    }

    @Override
    @Transactional
    public AgreementResponse uploadRevision(Long agreementId, String revisionNotes, MultipartFile documentFile) {
        AgreementEntity agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
        UserEntity uploader = currentUser();

        int nextVersion = agreement.getVersions().size() + 1;

        if (documentFile != null && !documentFile.isEmpty()) {
            DocumentResponse uploaded = documentService.upload(documentFile, uploader.getId());
            Document docEntity = documentRepository.findById(uploaded.id())
                    .orElseThrow(() -> new IllegalStateException("Document not found"));

            AgreementVersionEntity version = AgreementVersionEntity.builder()
                    .agreement(agreement)
                    .versionNumber(nextVersion)
                    .document(docEntity)
                    .uploadedAt(LocalDateTime.now())
                    .uploadedBy(uploader)
                    .versionNotes(revisionNotes)
                    .build();

            versionRepository.save(version);
            agreement.getVersions().add(version);
            agreement.setStatus(AgreementStatus.IN_REVIEW); // Return to review after revision
            agreement.setUpdatedAt(LocalDateTime.now());
        }

        return toResponse(agreementRepository.save(agreement));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponse> getMyAgreements() {
        return agreementRepository.findByCreatedById(currentUser().getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgreementResponse getAgreementById(Long id) {
        return toResponse(agreementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponse> getAgreementsForReview() {
        return agreementRepository.findByReviewerId(currentUser().getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AgreementResponse reviewAgreement(Long agreementId, ReviewAgreementRequest request) {
        AgreementEntity agreement = agreementRepository.findById(agreementId).orElseThrow();
        UserEntity reviewer = currentUser();

        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            AgreementCommentEntity comment = AgreementCommentEntity.builder()
                    .agreement(agreement)
                    .commentedBy(reviewer)
                    .commentText(request.getRemarks())
                    .createdAt(LocalDateTime.now())
                    .build();
            commentRepository.save(comment);
            agreement.getComments().add(comment);
        }

        if (request.getReviewStatus() == AgreementStatus.REVISION_REQUESTED ||
                request.getReviewStatus() == AgreementStatus.APPROVED) {
            agreement.setStatus(request.getReviewStatus());
            agreement.setUpdatedAt(LocalDateTime.now());
        }

        return toResponse(agreementRepository.save(agreement));
    }

    @Override
    @Transactional
    public AgreementResponse addComment(Long agreementId, String commentText) {
        AgreementEntity agreement = agreementRepository.findById(agreementId).orElseThrow();
        UserEntity user = currentUser();

        AgreementCommentEntity comment = AgreementCommentEntity.builder()
                .agreement(agreement)
                .commentedBy(user)
                .commentText(commentText)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        agreement.getComments().add(comment);

        return toResponse(agreement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponse> getAgreementsForApproval() {
        return agreementRepository.findByApproverId(currentUser().getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AgreementResponse approveOrReject(Long agreementId, ReviewAgreementRequest request) {
        AgreementEntity agreement = agreementRepository.findById(agreementId).orElseThrow();
        UserEntity approver = currentUser();

        if (request.getReviewStatus() != AgreementStatus.APPROVED
                && request.getReviewStatus() != AgreementStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status for approval process");
        }

        agreement.setStatus(request.getReviewStatus());
        agreement.setApprover(approver);
        agreement.setApprovalRemarks(request.getRemarks());
        agreement.setUpdatedAt(LocalDateTime.now());

        return toResponse(agreementRepository.save(agreement));
    }

    @Override
    @Transactional
    public AgreementResponse executeAgreement(Long agreementId) {
        AgreementEntity agreement = agreementRepository.findById(agreementId).orElseThrow();
        if (agreement.getStatus() != AgreementStatus.APPROVED) {
            throw new IllegalStateException("Only approved agreements can be executed");
        }
        agreement.setStatus(AgreementStatus.EXECUTED);
        agreement.setUpdatedAt(LocalDateTime.now());
        return toResponse(agreementRepository.save(agreement));
    }

    @Override
    @Transactional
    public AgreementResponse digitallySignAgreement(Long agreementId) {
        AgreementEntity agreement = agreementRepository.findById(agreementId).orElseThrow();
        if (agreement.getStatus() != AgreementStatus.APPROVED && agreement.getStatus() != AgreementStatus.EXECUTED) {
            throw new IllegalStateException("Agreement must be approved or executed to be signed");
        }
        agreement.setIsDigitallySigned(true);
        agreement.setUpdatedAt(LocalDateTime.now());
        return toResponse(agreementRepository.save(agreement));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponse> getAllAgreements() {
        return agreementRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }
}
