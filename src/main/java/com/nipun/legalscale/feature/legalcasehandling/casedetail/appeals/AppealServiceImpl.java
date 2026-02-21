package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.repository.*;
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
public class AppealServiceImpl implements AppealService {

    private final AppealDetailRepository appealDetailRepository;
    private final AppealDeadlineRepository deadlineRepository;
    private final AppealOutcomeRepository outcomeRepository;
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

        if (caseEntity.getCaseType() != CaseType.APPEALS) {
            throw new IllegalArgumentException(
                    "Appeals features are only available for APPEALS cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Appeals details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private AppealDetail findDetail(Long caseId) {
        return appealDetailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No appeal detail found for case " + caseId +
                                ". Please set the appeal details first."));
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private AppealDeadlineResponse toDeadlineResponse(AppealDeadline d) {
        LocalDate effectiveDate = d.getExtendedDeadlineDate() != null
                ? d.getExtendedDeadlineDate()
                : d.getDeadlineDate();
        boolean overdue = d.getStatus() == DeadlineStatus.PENDING
                && effectiveDate.isBefore(LocalDate.now());

        return AppealDeadlineResponse.builder()
                .id(d.getId())
                .caseId(d.getAppealDetail().getInitialCase().getId())
                .deadlineType(d.getDeadlineType())
                .deadlineDate(d.getDeadlineDate())
                .status(d.getStatus())
                .extendedDeadlineDate(d.getExtendedDeadlineDate())
                .notes(d.getNotes())
                .recordedByName(d.getRecordedBy().getFullName())
                .recordedByEmail(d.getRecordedBy().getEmail())
                .recordedAt(d.getRecordedAt())
                .statusUpdatedByName(d.getStatusUpdatedBy() != null ? d.getStatusUpdatedBy().getFullName() : null)
                .statusUpdatedAt(d.getStatusUpdatedAt())
                .overdue(overdue)
                .build();
    }

    private AppealOutcomeResponse toOutcomeResponse(AppealOutcome o) {
        return AppealOutcomeResponse.builder()
                .id(o.getId())
                .caseId(o.getAppealDetail().getInitialCase().getId())
                .judgingCourt(o.getJudgingCourt())
                .judgmentDate(o.getJudgmentDate())
                .judgment(o.getJudgment())
                .judgmentSummary(o.getJudgmentSummary())
                .remittalInstructions(o.getRemittalInstructions())
                .judgmentReference(o.getJudgmentReference())
                .notes(o.getNotes())
                .recordedByName(o.getRecordedBy().getFullName())
                .recordedByEmail(o.getRecordedBy().getEmail())
                .recordedAt(o.getRecordedAt())
                .lastUpdatedByName(o.getLastUpdatedBy() != null ? o.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(o.getLastUpdatedAt())
                .build();
    }

    private AppealDetailResponse toDetailResponse(AppealDetail detail) {
        List<AppealDeadlineResponse> deadlineResponses = deadlineRepository
                .findByAppealDetailIdOrderByDeadlineDateAsc(detail.getId())
                .stream().map(this::toDeadlineResponse).collect(Collectors.toList());

        long overdueCount = deadlineResponses.stream().filter(AppealDeadlineResponse::isOverdue).count();

        AppealOutcomeResponse outcomeResponse = outcomeRepository
                .findByAppealDetailId(detail.getId())
                .map(this::toOutcomeResponse)
                .orElse(null);

        InitialCaseEntity originalCase = detail.getOriginalCase();

        return AppealDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                // Original case linking
                .originalCaseId(originalCase != null ? originalCase.getId() : null)
                .originalCaseTitle(originalCase != null ? originalCase.getCaseTitle() : null)
                .originalCaseReferenceNumber(originalCase != null ? originalCase.getReferenceNumber() : null)
                .originalCaseReference(detail.getOriginalCaseReference())
                .appealCourt(detail.getAppealCourt())
                .filingDate(detail.getFilingDate())
                .groundsOfAppeal(detail.getGroundsOfAppeal())
                .notes(detail.getNotes())
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .deadlines(deadlineResponses)
                .overdueDeadlineCount(overdueCount)
                .outcome(outcomeResponse)
                .build();
    }

    // ─── Feature 1: Detail Management ────────────────────────────────────────────

    @Override
    @Transactional
    public AppealDetailResponse setAppealDetails(Long caseId, AppealDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();

        // Resolve the optional original case link
        InitialCaseEntity originalCase = null;
        if (request.getOriginalCaseId() != null) {
            originalCase = initialCaseRepository.findById(request.getOriginalCaseId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Original case not found with id: " + request.getOriginalCaseId()));
            // Guard: cannot link a case to itself
            if (originalCase.getId().equals(caseId)) {
                throw new IllegalArgumentException("An appeal cannot be linked to itself as the original case.");
            }
        }

        AppealDetail detail;
        if (appealDetailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setOriginalCase(originalCase);
            detail.setOriginalCaseReference(request.getOriginalCaseReference());
            detail.setAppealCourt(request.getAppealCourt());
            detail.setFilingDate(request.getFilingDate());
            detail.setGroundsOfAppeal(request.getGroundsOfAppeal());
            detail.setNotes(request.getNotes());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = AppealDetail.builder()
                    .initialCase(caseEntity)
                    .originalCase(originalCase)
                    .originalCaseReference(request.getOriginalCaseReference())
                    .appealCourt(request.getAppealCourt())
                    .filingDate(request.getFilingDate())
                    .groundsOfAppeal(request.getGroundsOfAppeal())
                    .notes(request.getNotes())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(appealDetailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public AppealDetailResponse getAppealDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Feature 2: Deadline Tracking ────────────────────────────────────────────

    @Override
    @Transactional
    public AppealDeadlineResponse addDeadline(Long caseId, AppealDeadlineRequest request) {
        findAndValidateCase(caseId);
        AppealDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        AppealDeadline deadline = AppealDeadline.builder()
                .appealDetail(detail)
                .deadlineType(request.getDeadlineType())
                .deadlineDate(request.getDeadlineDate())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toDeadlineResponse(deadlineRepository.save(deadline));
    }

    @Override
    @Transactional
    public AppealDeadlineResponse updateDeadlineStatus(Long caseId, Long deadlineId,
            DeadlineStatusUpdateRequest request) {
        findAndValidateCase(caseId);
        AppealDeadline deadline = deadlineRepository.findById(deadlineId)
                .orElseThrow(() -> new IllegalArgumentException("Deadline not found with id: " + deadlineId));

        if (!deadline.getAppealDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Deadline " + deadlineId + " does not belong to case " + caseId);
        }

        // Validate extended date is provided for EXTENDED status
        if (request.getStatus() == DeadlineStatus.EXTENDED && request.getExtendedDeadlineDate() == null) {
            throw new IllegalArgumentException(
                    "Extended deadline date is required when setting status to EXTENDED.");
        }

        UserEntity actor = currentUser();
        deadline.setStatus(request.getStatus());
        deadline.setExtendedDeadlineDate(request.getExtendedDeadlineDate());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            deadline.setNotes(request.getNotes());
        }
        deadline.setStatusUpdatedBy(actor);
        deadline.setStatusUpdatedAt(LocalDateTime.now());

        return toDeadlineResponse(deadlineRepository.save(deadline));
    }

    @Override
    @Transactional
    public void deleteDeadline(Long caseId, Long deadlineId) {
        findAndValidateCase(caseId);
        AppealDeadline deadline = deadlineRepository.findById(deadlineId)
                .orElseThrow(() -> new IllegalArgumentException("Deadline not found with id: " + deadlineId));

        if (!deadline.getAppealDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Deadline " + deadlineId + " does not belong to case " + caseId);
        }
        deadlineRepository.delete(deadline);
    }

    // ─── Feature 3: Outcome Recording ────────────────────────────────────────────

    @Override
    @Transactional
    public AppealOutcomeResponse recordOutcome(Long caseId, AppealOutcomeRequest request) {
        findAndValidateCase(caseId);
        AppealDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        AppealOutcome outcome;
        if (outcomeRepository.existsByAppealDetailId(detail.getId())) {
            outcome = outcomeRepository.findByAppealDetailId(detail.getId()).get();
            outcome.setJudgingCourt(request.getJudgingCourt());
            outcome.setJudgmentDate(request.getJudgmentDate());
            outcome.setJudgment(request.getJudgment());
            outcome.setJudgmentSummary(request.getJudgmentSummary());
            outcome.setRemittalInstructions(request.getRemittalInstructions());
            outcome.setJudgmentReference(request.getJudgmentReference());
            outcome.setNotes(request.getNotes());
            outcome.setLastUpdatedBy(actor);
            outcome.setLastUpdatedAt(LocalDateTime.now());
        } else {
            outcome = AppealOutcome.builder()
                    .appealDetail(detail)
                    .judgingCourt(request.getJudgingCourt())
                    .judgmentDate(request.getJudgmentDate())
                    .judgment(request.getJudgment())
                    .judgmentSummary(request.getJudgmentSummary())
                    .remittalInstructions(request.getRemittalInstructions())
                    .judgmentReference(request.getJudgmentReference())
                    .notes(request.getNotes())
                    .recordedBy(actor)
                    .recordedAt(LocalDateTime.now())
                    .build();
        }
        return toOutcomeResponse(outcomeRepository.save(outcome));
    }

    @Override
    @Transactional
    public void deleteOutcome(Long caseId) {
        findAndValidateCase(caseId);
        AppealDetail detail = findDetail(caseId);
        AppealOutcome outcome = outcomeRepository.findByAppealDetailId(detail.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No outcome recorded for case " + caseId));
        outcomeRepository.delete(outcome);
    }
}
