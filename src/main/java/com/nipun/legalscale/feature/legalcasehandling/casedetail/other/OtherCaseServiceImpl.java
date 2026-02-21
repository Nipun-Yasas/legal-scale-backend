package com.nipun.legalscale.feature.legalcasehandling.casedetail.other;

import com.nipun.legalscale.core.document.entity.Document;
import com.nipun.legalscale.core.document.repository.DocumentRepository;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.repository.*;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OtherCaseServiceImpl implements OtherCaseService {

    private final OtherCaseDetailRepository detailRepository;
    private final CaseAttributeRepository attributeRepository;
    private final CaseDocumentTemplateRepository templateRepository;
    private final InitialCaseRepository initialCaseRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    private static final String UNCATEGORISED = "Uncategorised";

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

        if (caseEntity.getCaseType() != CaseType.OTHER) {
            throw new IllegalArgumentException(
                    "Other Case features are only available for OTHER cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Other Case details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private OtherCaseDetail findDetail(Long caseId) {
        return detailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No detail found for case " + caseId +
                                ". Please set case details first."));
    }

    private void assertAttributeOwnership(CaseAttribute attr, Long detailId) {
        if (!attr.getOtherCaseDetail().getId().equals(detailId)) {
            throw new IllegalArgumentException("Attribute " + attr.getId() + " does not belong to this case.");
        }
    }

    private void assertTemplateOwnership(CaseDocumentTemplate tmpl, Long detailId) {
        if (!tmpl.getOtherCaseDetail().getId().equals(detailId)) {
            throw new IllegalArgumentException("Template " + tmpl.getId() + " does not belong to this case.");
        }
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private CaseAttributeResponse toAttributeResponse(CaseAttribute a) {
        return CaseAttributeResponse.builder()
                .id(a.getId())
                .caseId(a.getOtherCaseDetail().getInitialCase().getId())
                .attributeName(a.getAttributeName())
                .displayLabel(a.getDisplayLabel())
                .attributeValue(a.getAttributeValue())
                .dataType(a.getDataType())
                .category(a.getCategory())
                .displayOrder(a.getDisplayOrder())
                .required(a.isRequired())
                .notes(a.getNotes())
                .createdByName(a.getCreatedBy().getFullName())
                .createdByEmail(a.getCreatedBy().getEmail())
                .createdAt(a.getCreatedAt())
                .lastUpdatedByName(a.getLastUpdatedBy() != null ? a.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(a.getLastUpdatedAt())
                .build();
    }

    private CaseDocumentTemplateResponse toTemplateResponse(CaseDocumentTemplate t) {
        Document doc = t.getUploadedTemplate();
        return CaseDocumentTemplateResponse.builder()
                .id(t.getId())
                .caseId(t.getOtherCaseDetail().getInitialCase().getId())
                .templateName(t.getTemplateName())
                .description(t.getDescription())
                .templateContent(t.getTemplateContent())
                .version(t.getVersion())
                .status(t.getStatus())
                .notes(t.getNotes())
                .createdByName(t.getCreatedBy().getFullName())
                .createdByEmail(t.getCreatedBy().getEmail())
                .createdAt(t.getCreatedAt())
                .lastUpdatedByName(t.getLastUpdatedBy() != null ? t.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(t.getLastUpdatedAt())
                .uploadedTemplateDocumentId(doc != null ? doc.getId() : null)
                .uploadedTemplateFileName(doc != null ? doc.getFileName() : null)
                .uploadedTemplateUrl(doc != null ? doc.getFileUrl() : null)
                .build();
    }

    private OtherCaseDetailResponse toDetailResponse(OtherCaseDetail detail) {
        List<CaseAttributeResponse> attributes = attributeRepository
                .findByOtherCaseDetailIdOrderByCategoryAscDisplayOrderAscAttributeNameAsc(detail.getId())
                .stream().map(this::toAttributeResponse).collect(Collectors.toList());

        // Build grouped map: category → attributes
        Map<String, List<CaseAttributeResponse>> byCategory = attributes.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory() != null ? a.getCategory() : UNCATEGORISED));

        List<CaseDocumentTemplateResponse> templates = templateRepository
                .findByOtherCaseDetailIdOrderByCreatedAtDesc(detail.getId())
                .stream().map(this::toTemplateResponse).collect(Collectors.toList());

        return OtherCaseDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .caseNature(detail.getCaseNature())
                .description(detail.getDescription())
                .notes(detail.getNotes())
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .attributes(attributes)
                .attributesByCategory(byCategory)
                .totalAttributes(attributes.size())
                .templates(templates)
                .activeTemplates(
                        templateRepository.countByOtherCaseDetailIdAndStatus(detail.getId(), TemplateStatus.ACTIVE))
                .draftTemplates(
                        templateRepository.countByOtherCaseDetailIdAndStatus(detail.getId(), TemplateStatus.DRAFT))
                .archivedTemplates(
                        templateRepository.countByOtherCaseDetailIdAndStatus(detail.getId(), TemplateStatus.ARCHIVED))
                .build();
    }

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public OtherCaseDetailResponse setCaseDetails(Long caseId, OtherCaseDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();
        OtherCaseDetail detail;

        if (detailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setCaseNature(request.getCaseNature());
            detail.setDescription(request.getDescription());
            detail.setNotes(request.getNotes());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = OtherCaseDetail.builder()
                    .initialCase(caseEntity)
                    .caseNature(request.getCaseNature())
                    .description(request.getDescription())
                    .notes(request.getNotes())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(detailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public OtherCaseDetailResponse getCaseDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Feature 1: Configurable Attributes ──────────────────────────────────────

    @Override
    @Transactional
    public CaseAttributeResponse addAttribute(Long caseId, CaseAttributeRequest request) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Enforce unique attribute name within the case
        if (attributeRepository.existsByOtherCaseDetailIdAndAttributeName(
                detail.getId(), request.getAttributeName())) {
            throw new IllegalArgumentException(
                    "An attribute with name '" + request.getAttributeName() +
                            "' already exists for this case. Use a different key or update the existing attribute.");
        }

        CaseAttribute attribute = CaseAttribute.builder()
                .otherCaseDetail(detail)
                .attributeName(request.getAttributeName())
                .displayLabel(request.getDisplayLabel())
                .attributeValue(request.getAttributeValue())
                .dataType(request.getDataType())
                .category(request.getCategory())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .required(request.isRequired())
                .notes(request.getNotes())
                .createdBy(actor)
                .createdAt(LocalDateTime.now())
                .build();

        return toAttributeResponse(attributeRepository.save(attribute));
    }

    @Override
    @Transactional
    public CaseAttributeResponse updateAttribute(Long caseId, Long attributeId, CaseAttributeRequest request) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        CaseAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));
        assertAttributeOwnership(attribute, detail.getId());

        // Unique name check (excluding this record)
        if (attributeRepository.existsByOtherCaseDetailIdAndAttributeNameAndIdNot(
                detail.getId(), request.getAttributeName(), attributeId)) {
            throw new IllegalArgumentException(
                    "Another attribute with name '" + request.getAttributeName() + "' already exists for this case.");
        }

        UserEntity actor = currentUser();
        attribute.setAttributeName(request.getAttributeName());
        attribute.setDisplayLabel(request.getDisplayLabel());
        attribute.setAttributeValue(request.getAttributeValue());
        attribute.setDataType(request.getDataType());
        attribute.setCategory(request.getCategory());
        attribute.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        attribute.setRequired(request.isRequired());
        attribute.setNotes(request.getNotes());
        attribute.setLastUpdatedBy(actor);
        attribute.setLastUpdatedAt(LocalDateTime.now());

        return toAttributeResponse(attributeRepository.save(attribute));
    }

    @Override
    @Transactional
    public void deleteAttribute(Long caseId, Long attributeId) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        CaseAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));
        assertAttributeOwnership(attribute, detail.getId());
        attributeRepository.delete(attribute);
    }

    // ─── Feature 2: Document Templates ───────────────────────────────────────────

    private Document resolveUploadedTemplate(Long documentId) {
        if (documentId == null)
            return null;
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document not found with id: " + documentId +
                                ". Upload the file first via POST /api/documents/upload"));
    }

    @Override
    @Transactional
    public CaseDocumentTemplateResponse addTemplate(Long caseId, CaseDocumentTemplateRequest request) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // At least one of: inline content or uploaded file must be provided
        if ((request.getTemplateContent() == null || request.getTemplateContent().isBlank())
                && request.getUploadedTemplateDocumentId() == null) {
            throw new IllegalArgumentException(
                    "At least one of templateContent (inline) or uploadedTemplateDocumentId must be provided.");
        }

        CaseDocumentTemplate template = CaseDocumentTemplate.builder()
                .otherCaseDetail(detail)
                .templateName(request.getTemplateName())
                .description(request.getDescription())
                .templateContent(request.getTemplateContent())
                .version(request.getVersion() != null && !request.getVersion().isBlank()
                        ? request.getVersion()
                        : "v1.0")
                .uploadedTemplate(resolveUploadedTemplate(request.getUploadedTemplateDocumentId()))
                .notes(request.getNotes())
                .createdBy(actor)
                .createdAt(LocalDateTime.now())
                .build();

        return toTemplateResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public CaseDocumentTemplateResponse updateTemplate(Long caseId, Long templateId,
            CaseDocumentTemplateRequest request) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        CaseDocumentTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + templateId));
        assertTemplateOwnership(template, detail.getId());

        if ((request.getTemplateContent() == null || request.getTemplateContent().isBlank())
                && request.getUploadedTemplateDocumentId() == null) {
            throw new IllegalArgumentException(
                    "At least one of templateContent (inline) or uploadedTemplateDocumentId must be provided.");
        }

        UserEntity actor = currentUser();
        template.setTemplateName(request.getTemplateName());
        template.setDescription(request.getDescription());
        template.setTemplateContent(request.getTemplateContent());
        if (request.getVersion() != null && !request.getVersion().isBlank()) {
            template.setVersion(request.getVersion());
        }
        template.setUploadedTemplate(resolveUploadedTemplate(request.getUploadedTemplateDocumentId()));
        template.setNotes(request.getNotes());
        template.setLastUpdatedBy(actor);
        template.setLastUpdatedAt(LocalDateTime.now());

        return toTemplateResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public CaseDocumentTemplateResponse updateTemplateStatus(Long caseId, Long templateId,
            TemplateStatusUpdateRequest request) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        CaseDocumentTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + templateId));
        assertTemplateOwnership(template, detail.getId());

        UserEntity actor = currentUser();
        template.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            template.setNotes(request.getNotes());
        }
        template.setLastUpdatedBy(actor);
        template.setLastUpdatedAt(LocalDateTime.now());

        return toTemplateResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void deleteTemplate(Long caseId, Long templateId) {
        findAndValidateCase(caseId);
        OtherCaseDetail detail = findDetail(caseId);
        CaseDocumentTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + templateId));
        assertTemplateOwnership(template, detail.getId());
        templateRepository.delete(template);
    }
}
