package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.MoneyRecoveryDetailResponse;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionRequest;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.dto.RecoveryTransactionResponse;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity.MoneyRecoveryDetail;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity.RecoveryTransaction;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.repository.MoneyRecoveryDetailRepository;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.repository.RecoveryTransactionRepository;
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
public class MoneyRecoveryServiceImpl implements MoneyRecoveryService {

    private final MoneyRecoveryDetailRepository detailRepository;
    private final RecoveryTransactionRepository transactionRepository;
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

        if (caseEntity.getCaseType() != CaseType.MONEY_RECOVERY) {
            throw new IllegalArgumentException(
                    "Money Recovery features are only available for MONEY_RECOVERY cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }

        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Money Recovery details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }

        return caseEntity;
    }

    private MoneyRecoveryDetail findDetail(Long caseId) {
        return detailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No money recovery detail found for case " + caseId +
                                ". Please set the claim amount first."));
    }

    private RecoveryTransactionResponse toTransactionResponse(RecoveryTransaction t) {
        return RecoveryTransactionResponse.builder()
                .id(t.getId())
                .caseId(t.getMoneyRecoveryDetail().getInitialCase().getId())
                .amount(t.getAmount())
                .recoveryType(t.getRecoveryType())
                .transactionDate(t.getTransactionDate())
                .notes(t.getNotes())
                .recordedByName(t.getRecordedBy().getFullName())
                .recordedByEmail(t.getRecordedBy().getEmail())
                .recordedAt(t.getRecordedAt())
                .build();
    }

    private MoneyRecoveryDetailResponse toDetailResponse(MoneyRecoveryDetail detail) {
        BigDecimal totalRecovered = transactionRepository.sumAmountByDetailId(detail.getId());
        BigDecimal outstanding = detail.getTotalClaimAmount().subtract(totalRecovered);
        if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
            outstanding = BigDecimal.ZERO;
        }

        List<RecoveryTransactionResponse> transactions = transactionRepository
                .findByMoneyRecoveryDetailIdOrderByTransactionDateAsc(detail.getId())
                .stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());

        return MoneyRecoveryDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .totalClaimAmount(detail.getTotalClaimAmount())
                .totalRecoveredAmount(totalRecovered)
                .outstandingBalance(outstanding)
                .fullyRecovered(outstanding.compareTo(BigDecimal.ZERO) == 0)
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .transactions(transactions)
                .build();
    }

    // ─── Operations
    // ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MoneyRecoveryDetailResponse setClaimAmount(Long caseId, MoneyRecoveryDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();

        MoneyRecoveryDetail detail;

        if (detailRepository.existsByInitialCaseId(caseId)) {
            // Update existing claim amount
            detail = findDetail(caseId);
            detail.setTotalClaimAmount(request.getTotalClaimAmount());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            // Create for the first time
            detail = MoneyRecoveryDetail.builder()
                    .initialCase(caseEntity)
                    .totalClaimAmount(request.getTotalClaimAmount())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return toDetailResponse(detailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public MoneyRecoveryDetailResponse getMoneyRecoveryDetail(Long caseId) {
        // validate case type + status
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    @Override
    @Transactional
    public RecoveryTransactionResponse recordRecovery(Long caseId, RecoveryTransactionRequest request) {
        // Validate case type + status
        findAndValidateCase(caseId);
        MoneyRecoveryDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Guard: recovered amount must not exceed the claim amount
        BigDecimal alreadyRecovered = transactionRepository.sumAmountByDetailId(detail.getId());
        BigDecimal newTotal = alreadyRecovered.add(request.getAmount());
        if (newTotal.compareTo(detail.getTotalClaimAmount()) > 0) {
            throw new IllegalArgumentException(
                    String.format("Recording this amount (%.2f) would exceed the total claim amount (%.2f). " +
                            "Outstanding balance is %.2f.",
                            request.getAmount(),
                            detail.getTotalClaimAmount(),
                            detail.getTotalClaimAmount().subtract(alreadyRecovered)));
        }

        RecoveryTransaction transaction = RecoveryTransaction.builder()
                .moneyRecoveryDetail(detail)
                .amount(request.getAmount())
                .recoveryType(request.getRecoveryType())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toTransactionResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long caseId, Long transactionId) {
        // Validate case is ACTIVE and MONEY_RECOVERY type
        findAndValidateCase(caseId);

        RecoveryTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found with id: " + transactionId));

        // Ensure the transaction belongs to this case
        if (!transaction.getMoneyRecoveryDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException(
                    "Transaction " + transactionId + " does not belong to case " + caseId);
        }

        transactionRepository.delete(transaction);
    }
}
