package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.enums.SettlementStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.repository.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DamagesRecoveryServiceImpl implements DamagesRecoveryService {

    private final DamagesRecoveryDetailRepository detailRepository;
    private final DamageAssessmentRepository assessmentRepository;
    private final CompensationPaymentRepository paymentRepository;
    private final SettlementAgreementRepository settlementRepository;
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

        if (caseEntity.getCaseType() != CaseType.DAMAGES_RECOVERY) {
            throw new IllegalArgumentException(
                    "Damages Recovery features are only available for DAMAGES_RECOVERY cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Damages Recovery details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private DamagesRecoveryDetail findDetail(Long caseId) {
        return detailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No damages recovery detail found for case " + caseId +
                                ". Please set the compensation claimed amount first."));
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private DamageAssessmentResponse toAssessmentResponse(DamageAssessment a) {
        return DamageAssessmentResponse.builder()
                .id(a.getId())
                .caseId(a.getDamagesRecoveryDetail().getInitialCase().getId())
                .category(a.getCategory())
                .description(a.getDescription())
                .estimatedValue(a.getEstimatedValue())
                .assessorName(a.getAssessorName())
                .assessmentDate(a.getAssessmentDate())
                .status(a.getStatus())
                .notes(a.getNotes())
                .recordedByName(a.getRecordedBy().getFullName())
                .recordedByEmail(a.getRecordedBy().getEmail())
                .recordedAt(a.getRecordedAt())
                .statusUpdatedByName(a.getStatusUpdatedBy() != null ? a.getStatusUpdatedBy().getFullName() : null)
                .statusUpdatedAt(a.getStatusUpdatedAt())
                .build();
    }

    private CompensationPaymentResponse toPaymentResponse(CompensationPayment p) {
        return CompensationPaymentResponse.builder()
                .id(p.getId())
                .caseId(p.getDamagesRecoveryDetail().getInitialCase().getId())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentReference(p.getPaymentReference())
                .notes(p.getNotes())
                .recordedByName(p.getRecordedBy().getFullName())
                .recordedByEmail(p.getRecordedBy().getEmail())
                .recordedAt(p.getRecordedAt())
                .build();
    }

    private SettlementAgreementResponse toSettlementResponse(SettlementAgreement s) {
        return SettlementAgreementResponse.builder()
                .id(s.getId())
                .caseId(s.getDamagesRecoveryDetail().getInitialCase().getId())
                .agreedAmount(s.getAgreedAmount())
                .settlementDate(s.getSettlementDate())
                .terms(s.getTerms())
                .status(s.getStatus())
                .notes(s.getNotes())
                .proposedByName(s.getProposedBy().getFullName())
                .proposedByEmail(s.getProposedBy().getEmail())
                .proposedAt(s.getProposedAt())
                .statusUpdatedByName(s.getStatusUpdatedBy() != null ? s.getStatusUpdatedBy().getFullName() : null)
                .statusUpdatedAt(s.getStatusUpdatedAt())
                .build();
    }

    private DamagesRecoveryDetailResponse toDetailResponse(DamagesRecoveryDetail detail) {
        BigDecimal totalAssessed = assessmentRepository.sumEstimatedValueByDetailId(detail.getId());
        BigDecimal totalReceived = paymentRepository.sumAmountByDetailId(detail.getId());
        BigDecimal outstanding = detail.getTotalCompensationClaimed().subtract(totalReceived);
        if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
            outstanding = BigDecimal.ZERO;
        }

        List<DamageAssessmentResponse> assessments = assessmentRepository
                .findByDamagesRecoveryDetailIdOrderByAssessmentDateAsc(detail.getId())
                .stream().map(this::toAssessmentResponse).collect(Collectors.toList());

        List<CompensationPaymentResponse> payments = paymentRepository
                .findByDamagesRecoveryDetailIdOrderByPaymentDateAsc(detail.getId())
                .stream().map(this::toPaymentResponse).collect(Collectors.toList());

        SettlementAgreementResponse settlementResponse = settlementRepository
                .findByDamagesRecoveryDetailId(detail.getId())
                .map(this::toSettlementResponse)
                .orElse(null);

        return DamagesRecoveryDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .totalCompensationClaimed(detail.getTotalCompensationClaimed())
                .totalAssessedValue(totalAssessed)
                .totalCompensationReceived(totalReceived)
                .outstandingBalance(outstanding)
                .fullyCompensated(outstanding.compareTo(BigDecimal.ZERO) == 0)
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .assessments(assessments)
                .compensationPayments(payments)
                .settlementAgreement(settlementResponse)
                .build();
    }

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DamagesRecoveryDetailResponse setCompensationClaimed(Long caseId, DamagesRecoveryDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();
        DamagesRecoveryDetail detail;

        if (detailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setTotalCompensationClaimed(request.getTotalCompensationClaimed());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = DamagesRecoveryDetail.builder()
                    .initialCase(caseEntity)
                    .totalCompensationClaimed(request.getTotalCompensationClaimed())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(detailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public DamagesRecoveryDetailResponse getDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Damage Assessments
    // ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public DamageAssessmentResponse addAssessment(Long caseId, DamageAssessmentRequest request) {
        findAndValidateCase(caseId);
        DamagesRecoveryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        DamageAssessment assessment = DamageAssessment.builder()
                .damagesRecoveryDetail(detail)
                .category(request.getCategory())
                .description(request.getDescription())
                .estimatedValue(request.getEstimatedValue())
                .assessorName(request.getAssessorName())
                .assessmentDate(request.getAssessmentDate())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toAssessmentResponse(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional
    public DamageAssessmentResponse updateAssessmentStatus(Long caseId, Long assessmentId,
            AssessmentStatusUpdateRequest request) {
        findAndValidateCase(caseId);
        DamageAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + assessmentId));

        if (!assessment.getDamagesRecoveryDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Assessment " + assessmentId + " does not belong to case " + caseId);
        }

        UserEntity actor = currentUser();
        assessment.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            assessment.setNotes(request.getNotes());
        }
        assessment.setStatusUpdatedBy(actor);
        assessment.setStatusUpdatedAt(LocalDateTime.now());

        return toAssessmentResponse(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional
    public void deleteAssessment(Long caseId, Long assessmentId) {
        findAndValidateCase(caseId);
        DamageAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + assessmentId));

        if (!assessment.getDamagesRecoveryDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Assessment " + assessmentId + " does not belong to case " + caseId);
        }
        assessmentRepository.delete(assessment);
    }

    // ─── Compensation Payments
    // ────────────────────────────────────────────────────

    @Override
    @Transactional
    public CompensationPaymentResponse recordPayment(Long caseId, CompensationPaymentRequest request) {
        findAndValidateCase(caseId);
        DamagesRecoveryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Guard: payment must not exceed outstanding balance
        BigDecimal alreadyReceived = paymentRepository.sumAmountByDetailId(detail.getId());
        BigDecimal newTotal = alreadyReceived.add(request.getAmount());
        if (newTotal.compareTo(detail.getTotalCompensationClaimed()) > 0) {
            throw new IllegalArgumentException(
                    String.format("Recording this payment (%.2f) would exceed the total compensation claimed (%.2f). " +
                            "Outstanding balance is %.2f.",
                            request.getAmount(),
                            detail.getTotalCompensationClaimed(),
                            detail.getTotalCompensationClaimed().subtract(alreadyReceived)));
        }

        CompensationPayment payment = CompensationPayment.builder()
                .damagesRecoveryDetail(detail)
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .paymentReference(request.getPaymentReference())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void deletePayment(Long caseId, Long paymentId) {
        findAndValidateCase(caseId);
        CompensationPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));

        if (!payment.getDamagesRecoveryDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Payment " + paymentId + " does not belong to case " + caseId);
        }
        paymentRepository.delete(payment);
    }

    // ─── Settlement Management
    // ────────────────────────────────────────────────────

    @Override
    @Transactional
    public SettlementAgreementResponse proposeSettlement(Long caseId, SettlementAgreementRequest request) {
        findAndValidateCase(caseId);
        DamagesRecoveryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        SettlementAgreement settlement;

        if (settlementRepository.existsByDamagesRecoveryDetailId(detail.getId())) {
            // Update existing proposal
            settlement = settlementRepository.findByDamagesRecoveryDetailId(detail.getId()).get();
            settlement.setAgreedAmount(request.getAgreedAmount());
            settlement.setSettlementDate(request.getSettlementDate());
            settlement.setTerms(request.getTerms());
            settlement.setNotes(request.getNotes());
            settlement.setStatus(SettlementStatus.PROPOSED);
            settlement.setStatusUpdatedBy(actor);
            settlement.setStatusUpdatedAt(LocalDateTime.now());
        } else {
            settlement = SettlementAgreement.builder()
                    .damagesRecoveryDetail(detail)
                    .agreedAmount(request.getAgreedAmount())
                    .settlementDate(request.getSettlementDate())
                    .terms(request.getTerms())
                    .notes(request.getNotes())
                    .status(SettlementStatus.PROPOSED)
                    .proposedBy(actor)
                    .proposedAt(LocalDateTime.now())
                    .build();
        }
        return toSettlementResponse(settlementRepository.save(settlement));
    }

    @Override
    @Transactional
    public SettlementAgreementResponse updateSettlementStatus(Long caseId, SettlementStatus newStatus) {
        findAndValidateCase(caseId);
        DamagesRecoveryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        SettlementAgreement settlement = settlementRepository.findByDamagesRecoveryDetailId(detail.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No settlement agreement found for case " + caseId + ". Propose one first."));

        settlement.setStatus(newStatus);
        settlement.setStatusUpdatedBy(actor);
        settlement.setStatusUpdatedAt(LocalDateTime.now());

        return toSettlementResponse(settlementRepository.save(settlement));
    }
}
