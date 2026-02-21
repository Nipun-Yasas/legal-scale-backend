package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.DecisionStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.FindingSeverity;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.PanelMemberRole;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.repository.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryDetailRepository detailRepository;
    private final PanelMemberRepository panelMemberRepository;
    private final InquiryFindingRepository findingRepository;
    private final InquiryDecisionRepository decisionRepository;
    private final InitialCaseRepository initialCaseRepository;
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

        if (caseEntity.getCaseType() != CaseType.INQUIRIES) {
            throw new IllegalArgumentException(
                    "Inquiry features are only available for INQUIRIES cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Inquiry details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private InquiryDetail findDetail(Long caseId) {
        return detailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No inquiry detail found for case " + caseId +
                                ". Please set inquiry details first."));
    }

    private void assertBelongsToDetail(Long entityDetailId, Long detailId, String entityName) {
        if (!entityDetailId.equals(detailId)) {
            throw new IllegalArgumentException(entityName + " does not belong to this inquiry.");
        }
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private PanelMemberResponse toPanelMemberResponse(PanelMember m) {
        return PanelMemberResponse.builder()
                .id(m.getId())
                .caseId(m.getInquiryDetail().getInitialCase().getId())
                .memberName(m.getMemberName())
                .designation(m.getDesignation())
                .department(m.getDepartment())
                .role(m.getRole())
                .appointedDate(m.getAppointedDate())
                .contactDetails(m.getContactDetails())
                .notes(m.getNotes())
                .recordedByName(m.getRecordedBy().getFullName())
                .recordedByEmail(m.getRecordedBy().getEmail())
                .recordedAt(m.getRecordedAt())
                .build();
    }

    private InquiryFindingResponse toFindingResponse(InquiryFinding f) {
        return InquiryFindingResponse.builder()
                .id(f.getId())
                .caseId(f.getInquiryDetail().getInitialCase().getId())
                .findingNumber(f.getFindingNumber())
                .findingTitle(f.getFindingTitle())
                .findingDescription(f.getFindingDescription())
                .severity(f.getSeverity())
                .recommendation(f.getRecommendation())
                .notes(f.getNotes())
                .recordedByName(f.getRecordedBy().getFullName())
                .recordedByEmail(f.getRecordedBy().getEmail())
                .recordedAt(f.getRecordedAt())
                .lastUpdatedByName(f.getLastUpdatedBy() != null ? f.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(f.getLastUpdatedAt())
                .build();
    }

    private InquiryDecisionResponse toDecisionResponse(InquiryDecision d) {
        InquiryFinding related = d.getRelatedFinding();
        LocalDate today = LocalDate.now();
        boolean overdue = d.getTargetDate() != null
                && today.isAfter(d.getTargetDate())
                && (d.getStatus() == DecisionStatus.PENDING || d.getStatus() == DecisionStatus.IN_PROGRESS);

        return InquiryDecisionResponse.builder()
                .id(d.getId())
                .caseId(d.getInquiryDetail().getInitialCase().getId())
                .decisionTitle(d.getDecisionTitle())
                .decisionDetails(d.getDecisionDetails())
                .responsibleParty(d.getResponsibleParty())
                .targetDate(d.getTargetDate())
                .implementedDate(d.getImplementedDate())
                .status(d.getStatus())
                .notes(d.getNotes())
                .relatedFindingNumber(related != null ? related.getFindingNumber() : null)
                .relatedFindingTitle(related != null ? related.getFindingTitle() : null)
                .recordedByName(d.getRecordedBy().getFullName())
                .recordedByEmail(d.getRecordedBy().getEmail())
                .recordedAt(d.getRecordedAt())
                .statusUpdatedByName(d.getStatusUpdatedBy() != null ? d.getStatusUpdatedBy().getFullName() : null)
                .statusUpdatedAt(d.getStatusUpdatedAt())
                .overdue(overdue)
                .build();
    }

    private InquiryDetailResponse toDetailResponse(InquiryDetail detail) {
        List<PanelMemberResponse> members = panelMemberRepository
                .findByInquiryDetailIdOrderByRoleAscMemberNameAsc(detail.getId())
                .stream().map(this::toPanelMemberResponse).collect(Collectors.toList());

        List<InquiryFindingResponse> findings = findingRepository
                .findByInquiryDetailIdOrderByFindingNumberAsc(detail.getId())
                .stream().map(this::toFindingResponse).collect(Collectors.toList());

        List<InquiryDecisionResponse> decisions = decisionRepository
                .findByInquiryDetailIdOrderByRecordedAtAsc(detail.getId())
                .stream().map(this::toDecisionResponse).collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        boolean reportSubmitted = detail.getReportSubmittedDate() != null;
        boolean reportOverdue = !reportSubmitted
                && detail.getReportingDeadline() != null
                && today.isAfter(detail.getReportingDeadline());

        return InquiryDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .inquirySubject(detail.getInquirySubject())
                .commissionedBy(detail.getCommissionedBy())
                .commissionedDate(detail.getCommissionedDate())
                .termsOfReference(detail.getTermsOfReference())
                .reportingDeadline(detail.getReportingDeadline())
                .reportSubmittedDate(detail.getReportSubmittedDate())
                .reportSubmitted(reportSubmitted)
                .reportOverdue(reportOverdue)
                .notes(detail.getNotes())
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .panelMembers(members)
                .panelSize(members.size())
                .findings(findings)
                .totalFindings(findings.size())
                .criticalFindings(
                        findingRepository.countByInquiryDetailIdAndSeverity(detail.getId(), FindingSeverity.CRITICAL))
                .majorFindings(
                        findingRepository.countByInquiryDetailIdAndSeverity(detail.getId(), FindingSeverity.MAJOR))
                .minorFindings(
                        findingRepository.countByInquiryDetailIdAndSeverity(detail.getId(), FindingSeverity.MINOR))
                .decisions(decisions)
                .totalDecisions(decisions.size())
                .pendingDecisions(
                        decisionRepository.countByInquiryDetailIdAndStatus(detail.getId(), DecisionStatus.PENDING))
                .inProgressDecisions(
                        decisionRepository.countByInquiryDetailIdAndStatus(detail.getId(), DecisionStatus.IN_PROGRESS))
                .implementedDecisions(
                        decisionRepository.countByInquiryDetailIdAndStatus(detail.getId(), DecisionStatus.IMPLEMENTED))
                .overdueDecisions(decisions.stream().filter(InquiryDecisionResponse::isOverdue).count())
                .build();
    }

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InquiryDetailResponse setInquiryDetails(Long caseId, InquiryDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();
        InquiryDetail detail;

        if (detailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setInquirySubject(request.getInquirySubject());
            detail.setCommissionedBy(request.getCommissionedBy());
            detail.setCommissionedDate(request.getCommissionedDate());
            detail.setTermsOfReference(request.getTermsOfReference());
            detail.setReportingDeadline(request.getReportingDeadline());
            detail.setReportSubmittedDate(request.getReportSubmittedDate());
            detail.setNotes(request.getNotes());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = InquiryDetail.builder()
                    .initialCase(caseEntity)
                    .inquirySubject(request.getInquirySubject())
                    .commissionedBy(request.getCommissionedBy())
                    .commissionedDate(request.getCommissionedDate())
                    .termsOfReference(request.getTermsOfReference())
                    .reportingDeadline(request.getReportingDeadline())
                    .reportSubmittedDate(request.getReportSubmittedDate())
                    .notes(request.getNotes())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(detailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryDetailResponse getInquiryDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Feature 1: Panel Setup
    // ───────────────────────────────────────────────────

    @Override
    @Transactional
    public PanelMemberResponse addPanelMember(Long caseId, PanelMemberRequest request) {
        findAndValidateCase(caseId);
        InquiryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Enforce at most one CHAIRPERSON per panel
        if (request.getRole() == PanelMemberRole.CHAIRPERSON
                && panelMemberRepository.existsByInquiryDetailIdAndRole(detail.getId(), PanelMemberRole.CHAIRPERSON)) {
            throw new IllegalArgumentException(
                    "A CHAIRPERSON has already been appointed to this panel. " +
                            "Remove the existing chairperson before appointing a new one.");
        }

        PanelMember member = PanelMember.builder()
                .inquiryDetail(detail)
                .memberName(request.getMemberName())
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .role(request.getRole())
                .appointedDate(request.getAppointedDate())
                .contactDetails(request.getContactDetails())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toPanelMemberResponse(panelMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removePanelMember(Long caseId, Long memberId) {
        findAndValidateCase(caseId);
        PanelMember member = panelMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Panel member not found with id: " + memberId));
        assertBelongsToDetail(member.getInquiryDetail().getId(), findDetail(caseId).getId(),
                "Panel member " + memberId);
        panelMemberRepository.delete(member);
    }

    // ─── Feature 2: Findings ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public InquiryFindingResponse addFinding(Long caseId, InquiryFindingRequest request) {
        findAndValidateCase(caseId);
        InquiryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Auto-assign next finding number
        int nextNumber = findingRepository.findMaxFindingNumber(detail.getId()) + 1;

        InquiryFinding finding = InquiryFinding.builder()
                .inquiryDetail(detail)
                .findingNumber(nextNumber)
                .findingTitle(request.getFindingTitle())
                .findingDescription(request.getFindingDescription())
                .severity(request.getSeverity())
                .recommendation(request.getRecommendation())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toFindingResponse(findingRepository.save(finding));
    }

    @Override
    @Transactional
    public InquiryFindingResponse updateFinding(Long caseId, Long findingId, InquiryFindingRequest request) {
        findAndValidateCase(caseId);
        InquiryFinding finding = findingRepository.findById(findingId)
                .orElseThrow(() -> new IllegalArgumentException("Finding not found with id: " + findingId));
        assertBelongsToDetail(finding.getInquiryDetail().getId(), findDetail(caseId).getId(), "Finding " + findingId);

        UserEntity actor = currentUser();
        finding.setFindingTitle(request.getFindingTitle());
        finding.setFindingDescription(request.getFindingDescription());
        finding.setSeverity(request.getSeverity());
        finding.setRecommendation(request.getRecommendation());
        finding.setNotes(request.getNotes());
        finding.setLastUpdatedBy(actor);
        finding.setLastUpdatedAt(LocalDateTime.now());

        return toFindingResponse(findingRepository.save(finding));
    }

    @Override
    @Transactional
    public void deleteFinding(Long caseId, Long findingId) {
        findAndValidateCase(caseId);
        InquiryFinding finding = findingRepository.findById(findingId)
                .orElseThrow(() -> new IllegalArgumentException("Finding not found with id: " + findingId));
        assertBelongsToDetail(finding.getInquiryDetail().getId(), findDetail(caseId).getId(), "Finding " + findingId);
        findingRepository.delete(finding);
    }

    // ─── Feature 3: Decision Tracking ────────────────────────────────────────────

    @Override
    @Transactional
    public InquiryDecisionResponse addDecision(Long caseId, InquiryDecisionRequest request) {
        findAndValidateCase(caseId);
        InquiryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Optionally resolve the related finding
        InquiryFinding relatedFinding = null;
        if (request.getRelatedFindingId() != null) {
            relatedFinding = findingRepository.findById(request.getRelatedFindingId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Finding not found with id: " + request.getRelatedFindingId()));
            assertBelongsToDetail(relatedFinding.getInquiryDetail().getId(), detail.getId(),
                    "Finding " + request.getRelatedFindingId());
        }

        InquiryDecision decision = InquiryDecision.builder()
                .inquiryDetail(detail)
                .relatedFinding(relatedFinding)
                .decisionTitle(request.getDecisionTitle())
                .decisionDetails(request.getDecisionDetails())
                .responsibleParty(request.getResponsibleParty())
                .targetDate(request.getTargetDate())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toDecisionResponse(decisionRepository.save(decision));
    }

    @Override
    @Transactional
    public InquiryDecisionResponse updateDecisionStatus(Long caseId, Long decisionId,
            DecisionStatusUpdateRequest request) {
        findAndValidateCase(caseId);
        InquiryDecision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found with id: " + decisionId));
        assertBelongsToDetail(decision.getInquiryDetail().getId(), findDetail(caseId).getId(),
                "Decision " + decisionId);

        // Validate: IMPLEMENTED requires an implementedDate
        if (request.getStatus() == DecisionStatus.IMPLEMENTED && request.getImplementedDate() == null) {
            throw new IllegalArgumentException(
                    "An implemented date is required when marking a decision as IMPLEMENTED.");
        }

        UserEntity actor = currentUser();
        decision.setStatus(request.getStatus());
        decision.setImplementedDate(request.getImplementedDate());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            decision.setNotes(request.getNotes());
        }
        decision.setStatusUpdatedBy(actor);
        decision.setStatusUpdatedAt(LocalDateTime.now());

        return toDecisionResponse(decisionRepository.save(decision));
    }

    @Override
    @Transactional
    public void deleteDecision(Long caseId, Long decisionId) {
        findAndValidateCase(caseId);
        InquiryDecision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found with id: " + decisionId));
        assertBelongsToDetail(decision.getInquiryDetail().getId(), findDetail(caseId).getId(),
                "Decision " + decisionId);
        decisionRepository.delete(decision);
    }
}
