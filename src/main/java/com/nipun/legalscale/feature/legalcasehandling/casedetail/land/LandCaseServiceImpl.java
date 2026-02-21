package com.nipun.legalscale.feature.legalcasehandling.casedetail.land;

import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.repository.*;
import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseType;
import com.nipun.legalscale.feature.legalcasehandling.repository.InitialCaseRepository;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandCaseServiceImpl implements LandCaseService {

    private final LandDetailRepository landDetailRepository;
    private final OwnershipRecordRepository ownershipRecordRepository;
    private final LandDeedPlanRepository deedPlanRepository;
    private final InitialCaseRepository initialCaseRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    // ─── Helpers
    // ──────────────────────────────────────────────────────────────────

    private UserEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private InitialCaseEntity findAndValidateCase(Long caseId) {
        InitialCaseEntity caseEntity = initialCaseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + caseId));

        if (caseEntity.getCaseType() != CaseType.LAND) {
            throw new IllegalArgumentException(
                    "Land features are only available for LAND cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Land details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private LandDetail findDetail(Long caseId) {
        return landDetailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No land detail found for case " + caseId +
                                ". Please set the land details first."));
    }

    private void assertBelongsToCase(Long entityCaseId, Long caseId, String entityName) {
        if (!entityCaseId.equals(caseId)) {
            throw new IllegalArgumentException(entityName + " does not belong to case " + caseId);
        }
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private OwnershipRecordResponse toOwnershipResponse(OwnershipRecord r) {
        return OwnershipRecordResponse.builder()
                .id(r.getId())
                .caseId(r.getLandDetail().getInitialCase().getId())
                .ownerName(r.getOwnerName())
                .ownerIdentificationNumber(r.getOwnerIdentificationNumber())
                .ownerAddress(r.getOwnerAddress())
                .ownershipType(r.getOwnershipType())
                .ownershipStartDate(r.getOwnershipStartDate())
                .ownershipEndDate(r.getOwnershipEndDate())
                .currentOwner(r.getOwnershipEndDate() == null)
                .deedReference(r.getDeedReference())
                .notes(r.getNotes())
                .recordedByName(r.getRecordedBy().getFullName())
                .recordedByEmail(r.getRecordedBy().getEmail())
                .recordedAt(r.getRecordedAt())
                .lastUpdatedByName(r.getLastUpdatedBy() != null ? r.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(r.getLastUpdatedAt())
                .build();
    }

    private LandDeedPlanResponse toDeedPlanResponse(LandDeedPlan dp) {
        Document doc = dp.getUploadedDocument();
        return LandDeedPlanResponse.builder()
                .id(dp.getId())
                .caseId(dp.getLandDetail().getInitialCase().getId())
                .documentType(dp.getDocumentType())
                .documentReference(dp.getDocumentReference())
                .issueDate(dp.getIssueDate())
                .issuingAuthority(dp.getIssuingAuthority())
                .notes(dp.getNotes())
                .recordedByName(dp.getRecordedBy().getFullName())
                .recordedByEmail(dp.getRecordedBy().getEmail())
                .recordedAt(dp.getRecordedAt())
                .uploadedDocumentId(doc != null ? doc.getId() : null)
                .uploadedDocumentName(doc != null ? doc.getFileName() : null)
                .uploadedDocumentUrl(doc != null ? doc.getFileUrl() : null)
                .build();
    }

    private LandDetailResponse toDetailResponse(LandDetail detail) {
        List<OwnershipRecordResponse> history = ownershipRecordRepository
                .findByLandDetailIdOrderByOwnershipStartDateAsc(detail.getId())
                .stream().map(this::toOwnershipResponse).collect(Collectors.toList());

        OwnershipRecordResponse currentOwner = history.stream()
                .filter(OwnershipRecordResponse::isCurrentOwner)
                .findFirst()
                .orElse(null);

        List<LandDeedPlanResponse> deedsAndPlans = deedPlanRepository
                .findByLandDetailIdOrderByIssueDateDesc(detail.getId())
                .stream().map(this::toDeedPlanResponse).collect(Collectors.toList());

        return LandDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .landReferenceNumber(detail.getLandReferenceNumber())
                .surveyPlanNumber(detail.getSurveyPlanNumber())
                .lotNumber(detail.getLotNumber())
                .planNumber(detail.getPlanNumber())
                .extent(detail.getExtent())
                .extentUnit(detail.getExtentUnit())
                .province(detail.getProvince())
                .district(detail.getDistrict())
                .dsDivision(detail.getDsDivision())
                .gnDivision(detail.getGnDivision())
                .address(detail.getAddress())
                .landRegistryDivision(detail.getLandRegistryDivision())
                .notes(detail.getNotes())
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .ownershipHistory(history)
                .currentOwner(currentOwner)
                .deedsAndPlans(deedsAndPlans)
                .build();
    }

    // ─── Feature 1: Land Reference Management ────────────────────────────────────

    @Override
    @Transactional
    public LandDetailResponse setLandDetails(Long caseId, LandDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();

        // Uniqueness check: no two cases should share the same land reference number
        if (landDetailRepository.existsByLandReferenceNumberAndInitialCaseIdNot(
                request.getLandReferenceNumber(), caseId)) {
            throw new IllegalArgumentException(
                    "Land reference number '" + request.getLandReferenceNumber() +
                            "' is already registered under another case.");
        }

        LandDetail detail;
        if (landDetailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setLandReferenceNumber(request.getLandReferenceNumber());
            detail.setSurveyPlanNumber(request.getSurveyPlanNumber());
            detail.setLotNumber(request.getLotNumber());
            detail.setPlanNumber(request.getPlanNumber());
            detail.setExtent(request.getExtent());
            detail.setExtentUnit(request.getExtentUnit());
            detail.setProvince(request.getProvince());
            detail.setDistrict(request.getDistrict());
            detail.setDsDivision(request.getDsDivision());
            detail.setGnDivision(request.getGnDivision());
            detail.setAddress(request.getAddress());
            detail.setLandRegistryDivision(request.getLandRegistryDivision());
            detail.setNotes(request.getNotes());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = LandDetail.builder()
                    .initialCase(caseEntity)
                    .landReferenceNumber(request.getLandReferenceNumber())
                    .surveyPlanNumber(request.getSurveyPlanNumber())
                    .lotNumber(request.getLotNumber())
                    .planNumber(request.getPlanNumber())
                    .extent(request.getExtent())
                    .extentUnit(request.getExtentUnit())
                    .province(request.getProvince())
                    .district(request.getDistrict())
                    .dsDivision(request.getDsDivision())
                    .gnDivision(request.getGnDivision())
                    .address(request.getAddress())
                    .landRegistryDivision(request.getLandRegistryDivision())
                    .notes(request.getNotes())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(landDetailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public LandDetailResponse getLandDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Feature 2: Ownership History ────────────────────────────────────────────

    @Override
    @Transactional
    public OwnershipRecordResponse addOwnershipRecord(Long caseId, OwnershipRecordRequest request) {
        findAndValidateCase(caseId);
        LandDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Validate date range
        if (request.getOwnershipEndDate() != null
                && !request.getOwnershipEndDate().isAfter(request.getOwnershipStartDate())) {
            throw new IllegalArgumentException(
                    "Ownership end date must be after the start date.");
        }

        OwnershipRecord record = OwnershipRecord.builder()
                .landDetail(detail)
                .ownerName(request.getOwnerName())
                .ownerIdentificationNumber(request.getOwnerIdentificationNumber())
                .ownerAddress(request.getOwnerAddress())
                .ownershipType(request.getOwnershipType())
                .ownershipStartDate(request.getOwnershipStartDate())
                .ownershipEndDate(request.getOwnershipEndDate())
                .deedReference(request.getDeedReference())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toOwnershipResponse(ownershipRecordRepository.save(record));
    }

    @Override
    @Transactional
    public OwnershipRecordResponse updateOwnershipRecord(Long caseId, Long recordId, OwnershipRecordRequest request) {
        findAndValidateCase(caseId);
        OwnershipRecord record = ownershipRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Ownership record not found with id: " + recordId));
        assertBelongsToCase(record.getLandDetail().getInitialCase().getId(), caseId, "Ownership record " + recordId);

        if (request.getOwnershipEndDate() != null
                && !request.getOwnershipEndDate().isAfter(request.getOwnershipStartDate())) {
            throw new IllegalArgumentException("Ownership end date must be after the start date.");
        }

        UserEntity actor = currentUser();
        record.setOwnerName(request.getOwnerName());
        record.setOwnerIdentificationNumber(request.getOwnerIdentificationNumber());
        record.setOwnerAddress(request.getOwnerAddress());
        record.setOwnershipType(request.getOwnershipType());
        record.setOwnershipStartDate(request.getOwnershipStartDate());
        record.setOwnershipEndDate(request.getOwnershipEndDate());
        record.setDeedReference(request.getDeedReference());
        record.setNotes(request.getNotes());
        record.setLastUpdatedBy(actor);
        record.setLastUpdatedAt(LocalDateTime.now());

        return toOwnershipResponse(ownershipRecordRepository.save(record));
    }

    @Override
    @Transactional
    public void deleteOwnershipRecord(Long caseId, Long recordId) {
        findAndValidateCase(caseId);
        OwnershipRecord record = ownershipRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Ownership record not found with id: " + recordId));
        assertBelongsToCase(record.getLandDetail().getInitialCase().getId(), caseId, "Ownership record " + recordId);
        ownershipRecordRepository.delete(record);
    }

    // ─── Feature 3: Deed & Plan Management ───────────────────────────────────────

    @Override
    @Transactional
    public LandDeedPlanResponse addDeedPlan(Long caseId, LandDeedPlanRequest request) {
        findAndValidateCase(caseId);
        LandDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Optionally resolve the uploaded document
        Document uploadedDoc = null;
        if (request.getUploadedDocumentId() != null) {
            uploadedDoc = documentRepository.findById(request.getUploadedDocumentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Document not found with id: " + request.getUploadedDocumentId() +
                                    ". Upload the file first via POST /api/documents/upload"));
        }

        LandDeedPlan deedPlan = LandDeedPlan.builder()
                .landDetail(detail)
                .documentType(request.getDocumentType())
                .documentReference(request.getDocumentReference())
                .issueDate(request.getIssueDate())
                .issuingAuthority(request.getIssuingAuthority())
                .uploadedDocument(uploadedDoc)
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toDeedPlanResponse(deedPlanRepository.save(deedPlan));
    }

    @Override
    @Transactional
    public void deleteDeedPlan(Long caseId, Long deedPlanId) {
        findAndValidateCase(caseId);
        LandDeedPlan deedPlan = deedPlanRepository.findById(deedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Deed/Plan not found with id: " + deedPlanId));
        assertBelongsToCase(deedPlan.getLandDetail().getInitialCase().getId(), caseId, "Deed/Plan " + deedPlanId);
        deedPlanRepository.delete(deedPlan);
    }
}
