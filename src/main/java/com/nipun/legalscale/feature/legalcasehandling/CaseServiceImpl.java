package com.nipun.legalscale.feature.legalcasehandling;

import com.nipun.legalscale.core.document.DocumentService;
import com.nipun.legalscale.core.document.dto.DocumentResponse;
import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import com.nipun.legalscale.feature.legalcasehandling.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.entity.CaseCommentEntity;
import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import com.nipun.legalscale.feature.legalcasehandling.repository.CaseCommentRepository;
import com.nipun.legalscale.feature.legalcasehandling.repository.InitialCaseRepository;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
public class CaseServiceImpl implements CaseService {

        private final InitialCaseRepository initialCaseRepository;
        private final CaseCommentRepository caseCommentRepository;
        private final UserRepository userRepository;
        private final DocumentRepository documentRepository;
        private final DocumentService documentService;

        // ─── Helpers
        // ──────────────────────────────────────────────────────────────────

        private UserEntity currentUser() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                return userRepository.findByEmail(auth.getName())
                                .orElseThrow(() -> new IllegalStateException(
                                                "Authenticated user not found in database"));
        }

        private InitialCaseEntity findCase(Long id) {
                return initialCaseRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + id));
        }

        private DocumentResponse toDocumentResponse(Document d) {
                return new DocumentResponse(
                                d.getId(),
                                d.getFileName(),
                                d.getFileType(),
                                d.getFileUrl(),
                                d.getUploadDate(),
                                d.getUploadedByUserId());
        }

        private CaseCommentResponse toCommentResponse(CaseCommentEntity c) {
                return CaseCommentResponse.builder()
                                .id(c.getId())
                                .caseId(c.getInitialCase().getId())
                                .commentedByName(c.getCommentedBy().getFullName())
                                .commentedByEmail(c.getCommentedBy().getEmail())
                                .comment(c.getComment())
                                .commentedAt(c.getCommentedAt())
                                .build();
        }

        private CaseResponse toCaseResponse(InitialCaseEntity c) {
                List<CaseCommentResponse> comments = c.getComments().stream()
                                .map(this::toCommentResponse)
                                .collect(Collectors.toList());

                List<DocumentResponse> attachments = c.getSupportingAttachments().stream()
                                .map(this::toDocumentResponse)
                                .collect(Collectors.toList());

                return CaseResponse.builder()
                                .id(c.getId())
                                .caseTitle(c.getCaseTitle())
                                .caseType(c.getCaseType())
                                .referenceNumber(c.getReferenceNumber())
                                .partiesInvolved(c.getPartiesInvolved())
                                .natureOfCase(c.getNatureOfCase())
                                .dateOfOccurrenceOrFiling(c.getDateOfOccurrenceOrFiling())
                                .courtOrAuthority(c.getCourtOrAuthority())
                                .financialExposure(c.getFinancialExposure())
                                .summaryOfFacts(c.getSummaryOfFacts())
                                .status(c.getStatus())
                                // created
                                .createdSupervisorName(c.getCreatedSupervisor().getFullName())
                                .createdSupervisorEmail(c.getCreatedSupervisor().getEmail())
                                .createdAt(c.getCreatedAt())
                                // assignment
                                .assignedOfficerName(
                                                c.getAssignedOfficer() != null ? c.getAssignedOfficer().getFullName()
                                                                : null)
                                .assignedOfficerEmail(c.getAssignedOfficer() != null ? c.getAssignedOfficer().getEmail()
                                                : null)
                                .assignedAt(c.getAssignedAt())
                                // approval
                                .approvedByName(c.getApprovedBy() != null ? c.getApprovedBy().getFullName() : null)
                                .approvedByEmail(c.getApprovedBy() != null ? c.getApprovedBy().getEmail() : null)
                                .approvedAt(c.getApprovedAt())
                                // closure
                                .closedByName(c.getClosedBy() != null ? c.getClosedBy().getFullName() : null)
                                .closedByEmail(c.getClosedBy() != null ? c.getClosedBy().getEmail() : null)
                                .closedAt(c.getClosedAt())
                                .closingRemarks(c.getClosingRemarks())
                                // comments & attachments
                                .comments(comments)
                                .supportingAttachments(attachments)
                                .build();
        }

        // ─── Case Creation
        // ────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public CaseResponse createCase(CreateCaseRequest request, List<MultipartFile> attachments) {
                if (initialCaseRepository.existsByReferenceNumber(request.getReferenceNumber())) {
                        throw new IllegalArgumentException("A case with reference number '"
                                        + request.getReferenceNumber() + "' already exists");
                }

                UserEntity creator = currentUser();

                InitialCaseEntity caseEntity = InitialCaseEntity.builder()
                                .caseTitle(request.getCaseTitle())
                                .caseType(request.getCaseType())
                                .referenceNumber(request.getReferenceNumber())
                                .partiesInvolved(request.getPartiesInvolved())
                                .natureOfCase(request.getNatureOfCase())
                                .dateOfOccurrenceOrFiling(request.getDateOfOccurrenceOrFiling())
                                .courtOrAuthority(request.getCourtOrAuthority())
                                .financialExposure(request.getFinancialExposure())
                                .summaryOfFacts(request.getSummaryOfFacts())
                                .status(CaseStatus.NEW)
                                .createdSupervisor(creator)
                                .createdAt(LocalDateTime.now())
                                .build();

                // Persist first so we have an ID to link documents against
                InitialCaseEntity saved = initialCaseRepository.save(caseEntity);

                // Attach any files supplied at creation time
                if (attachments != null) {
                        for (MultipartFile file : attachments) {
                                if (file != null && !file.isEmpty()) {
                                        DocumentResponse uploaded = documentService.upload(file, creator.getId());
                                        Document document = documentRepository.findById(uploaded.id())
                                                        .orElseThrow(() -> new IllegalStateException(
                                                                        "Document was not persisted correctly"));
                                        saved.getSupportingAttachments().add(document);
                                }
                        }
                        if (!attachments.isEmpty()) {
                                saved = initialCaseRepository.save(saved);
                        }
                }

                return toCaseResponse(saved);
        }

        // ─── Read
        // ─────────────────────────────────────────────────────────────────────

        @Override
        @Transactional(readOnly = true)
        public List<CaseResponse> getAllNewCases() {
                return initialCaseRepository.findByStatusAndAssignedOfficerIsNull(CaseStatus.NEW).stream()
                                .map(this::toCaseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<CaseResponse> getAllCases() {
                return initialCaseRepository.findAll().stream()
                                .map(this::toCaseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public CaseResponse getCaseById(Long id) {
                return toCaseResponse(findCase(id));
        }

        @Override
        @Transactional(readOnly = true)
        public List<CaseResponse> getCasesAssignedToCurrentOfficer() {
                UserEntity officer = currentUser();
                return initialCaseRepository.findByAssignedOfficerId(officer.getId()).stream()
                                .map(this::toCaseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<CaseResponse> getCasesCreatedByCurrentSupervisor() {
                UserEntity supervisor = currentUser();
                return initialCaseRepository.findByCreatedSupervisorId(supervisor.getId()).stream()
                                .map(this::toCaseResponse)
                                .collect(Collectors.toList());
        }

        // ─── Supervisor Actions
        // ───────────────────────────────────────────────────────

        @Override
        @Transactional
        public CaseResponse assignCaseToOfficer(Long caseId, AssignCaseRequest request) {
                UserEntity supervisor = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                UserEntity officer = userRepository.findById(request.getOfficerId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Officer not found with id: " + request.getOfficerId()));

                // Verify the target user is a LEGAL_OFFICER
                String officerRole = officer.getRole().getRoleName().name();
                if (!officerRole.equals("LEGAL_OFFICER")) {
                        throw new IllegalArgumentException("The selected user is not a Legal Officer");
                }

                caseEntity.setAssignedOfficer(officer);

                caseEntity.setAssignedAt(LocalDateTime.now());

                return toCaseResponse(initialCaseRepository.save(caseEntity));
        }

        @Override
        @Transactional
        public CaseResponse supervisorUpdateStatus(Long caseId, UpdateCaseStatusRequest request) {
                UserEntity supervisor = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                applyStatusChange(caseEntity, request, supervisor);

                return toCaseResponse(initialCaseRepository.save(caseEntity));
        }

        @Override
        @Transactional
        public CaseCommentResponse supervisorAddComment(Long caseId, AddCommentRequest request) {
                UserEntity supervisor = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                CaseCommentEntity comment = CaseCommentEntity.builder()
                                .initialCase(caseEntity)
                                .commentedBy(supervisor)
                                .comment(request.getComment())
                                .commentedAt(LocalDateTime.now())
                                .build();

                return toCommentResponse(caseCommentRepository.save(comment));
        }

        // ─── Officer Actions
        // ──────────────────────────────────────────────────────────

        @Override
        @Transactional
        public CaseResponse officerUpdateStatus(Long caseId, UpdateCaseStatusRequest request) {
                UserEntity officer = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                // Officers can only update cases assigned to them
                if (caseEntity.getAssignedOfficer() == null
                                || !caseEntity.getAssignedOfficer().getId().equals(officer.getId())) {
                        throw new AccessDeniedException("You are not assigned to this case");
                }

                applyStatusChange(caseEntity, request, officer);

                return toCaseResponse(initialCaseRepository.save(caseEntity));
        }

        @Override
        @Transactional
        public CaseCommentResponse officerAddComment(Long caseId, AddCommentRequest request) {
                UserEntity officer = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                // Officers can only comment on cases assigned to them
                if (caseEntity.getAssignedOfficer() == null
                                || !caseEntity.getAssignedOfficer().getId().equals(officer.getId())) {
                        throw new AccessDeniedException("You are not assigned to this case");
                }

                CaseCommentEntity comment = CaseCommentEntity.builder()
                                .initialCase(caseEntity)
                                .commentedBy(officer)
                                .comment(request.getComment())
                                .commentedAt(LocalDateTime.now())
                                .build();

                return toCommentResponse(caseCommentRepository.save(comment));
        }

        // ─── Attachment Management
        // ─────────────────────────────────────────────────────

        @Override
        @Transactional
        public CaseResponse attachDocument(Long caseId, MultipartFile file) {
                UserEntity uploader = currentUser();
                InitialCaseEntity caseEntity = findCase(caseId);

                // Delegate to the generic DocumentService to store the file
                DocumentResponse uploaded = documentService.upload(file, uploader.getId());

                // Fetch the persisted Document entity and link it to this case
                Document document = documentRepository.findById(uploaded.id())
                                .orElseThrow(() -> new IllegalStateException("Document was not persisted correctly"));

                caseEntity.getSupportingAttachments().add(document);
                return toCaseResponse(initialCaseRepository.save(caseEntity));
        }

        @Override
        @Transactional
        public CaseResponse removeAttachment(Long caseId, Long documentId) {
                InitialCaseEntity caseEntity = findCase(caseId);

                boolean removed = caseEntity.getSupportingAttachments()
                                .removeIf(doc -> doc.getId().equals(documentId));

                if (!removed) {
                        throw new IllegalArgumentException(
                                        "Document " + documentId + " is not attached to case " + caseId);
                }

                return toCaseResponse(initialCaseRepository.save(caseEntity));
        }

        // ─── Shared Status Transition Logic
        // ───────────────────────────────────────────

        /**
         * Applies a status change with the appropriate audit fields.
         * Rules:
         * - ACTIVE → records approvedBy / approvedAt
         * - CLOSED → requires closingRemarks, records closedBy / closedAt
         * - ON_HOLD → no extra audit fields
         */
        private void applyStatusChange(InitialCaseEntity caseEntity,
                        UpdateCaseStatusRequest request,
                        UserEntity actor) {
                CaseStatus newStatus = request.getStatus();

                switch (newStatus) {
                        case ACTIVE -> {
                                caseEntity.setApprovedBy(actor);
                                caseEntity.setApprovedAt(LocalDateTime.now());
                        }
                        case CLOSED -> {
                                if (request.getClosingRemarks() == null || request.getClosingRemarks().isBlank()) {
                                        throw new IllegalArgumentException(
                                                        "Closing remarks are required when closing a case");
                                }
                                caseEntity.setClosedBy(actor);
                                caseEntity.setClosedAt(LocalDateTime.now());
                                caseEntity.setClosingRemarks(request.getClosingRemarks());
                        }
                        default -> {
                                /* ON_HOLD, NEW – no extra audit needed */ }
                }

                caseEntity.setStatus(newStatus);
        }
}
