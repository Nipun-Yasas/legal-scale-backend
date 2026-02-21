package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.dto.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity.*;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.ChargeStatus;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.HearingOutcome;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.PleaType;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.repository.*;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CriminalCaseServiceImpl implements CriminalCaseService {

    private final CriminalCaseDetailRepository detailRepository;
    private final CriminalChargeRepository chargeRepository;
    private final CourtHearingRepository hearingRepository;
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

        if (caseEntity.getCaseType() != CaseType.CRIMINAL) {
            throw new IllegalArgumentException(
                    "Criminal case features are only available for CRIMINAL cases. " +
                            "This case is type: " + caseEntity.getCaseType());
        }
        if (caseEntity.getStatus() != CaseStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Criminal case details can only be managed for ACTIVE cases. " +
                            "Current status: " + caseEntity.getStatus());
        }
        return caseEntity;
    }

    private CriminalCaseDetail findDetail(Long caseId) {
        return detailRepository.findByInitialCaseId(caseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No criminal case detail found for case " + caseId +
                                ". Please set case details first."));
    }

    private void assertChargeOwnership(CriminalCharge charge, Long caseId) {
        if (!charge.getCriminalCaseDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Charge " + charge.getId() + " does not belong to case " + caseId);
        }
    }

    private void assertHearingOwnership(CourtHearing hearing, Long caseId) {
        if (!hearing.getCriminalCaseDetail().getInitialCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Hearing " + hearing.getId() + " does not belong to case " + caseId);
        }
    }

    // ─── Mappers
    // ──────────────────────────────────────────────────────────────────

    private CriminalChargeResponse toChargeResponse(CriminalCharge c) {
        return CriminalChargeResponse.builder()
                .id(c.getId())
                .caseId(c.getCriminalCaseDetail().getInitialCase().getId())
                .statute(c.getStatute())
                .section(c.getSection())
                .offenceName(c.getOffenceName())
                .offenceDescription(c.getOffenceDescription())
                .maximumPenalty(c.getMaximumPenalty())
                .plea(c.getPlea())
                .status(c.getStatus())
                .outcomeDetails(c.getOutcomeDetails())
                .notes(c.getNotes())
                .recordedByName(c.getRecordedBy().getFullName())
                .recordedByEmail(c.getRecordedBy().getEmail())
                .recordedAt(c.getRecordedAt())
                .lastUpdatedByName(c.getLastUpdatedBy() != null ? c.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(c.getLastUpdatedAt())
                .build();
    }

    private CourtHearingResponse toHearingResponse(CourtHearing h) {
        return CourtHearingResponse.builder()
                .id(h.getId())
                .caseId(h.getCriminalCaseDetail().getInitialCase().getId())
                .hearingDate(h.getHearingDate())
                .hearingType(h.getHearingType())
                .presidingJudge(h.getPresidingJudge())
                .proceedingsSummary(h.getProceedingsSummary())
                .outcome(h.getOutcome())
                .nextHearingDate(h.getNextHearingDate())
                .nextHearingPurpose(h.getNextHearingPurpose())
                .notes(h.getNotes())
                .recordedByName(h.getRecordedBy().getFullName())
                .recordedByEmail(h.getRecordedBy().getEmail())
                .recordedAt(h.getRecordedAt())
                .lastUpdatedByName(h.getLastUpdatedBy() != null ? h.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedAt(h.getLastUpdatedAt())
                .build();
    }

    private CriminalCaseDetailResponse toDetailResponse(CriminalCaseDetail detail) {
        List<CriminalChargeResponse> charges = chargeRepository
                .findByCriminalCaseDetailIdOrderByRecordedAtAsc(detail.getId())
                .stream().map(this::toChargeResponse).collect(Collectors.toList());

        List<CourtHearingResponse> hearings = hearingRepository
                .findByCriminalCaseDetailIdOrderByHearingDateAsc(detail.getId())
                .stream().map(this::toHearingResponse).collect(Collectors.toList());

        // Derive next hearing from most recent ADJOURNED hearing (latest hearing date
        // first)
        CourtHearingResponse latestAdjourned = hearings.stream()
                .filter(h -> h.getOutcome() == HearingOutcome.ADJOURNED && h.getNextHearingDate() != null)
                .max(Comparator.comparing(CourtHearingResponse::getHearingDate))
                .orElse(null);

        return CriminalCaseDetailResponse.builder()
                .id(detail.getId())
                .caseId(detail.getInitialCase().getId())
                .caseTitle(detail.getInitialCase().getCaseTitle())
                .referenceNumber(detail.getInitialCase().getReferenceNumber())
                .accusedName(detail.getAccusedName())
                .accusedIdNumber(detail.getAccusedIdNumber())
                .accusedAddress(detail.getAccusedAddress())
                .court(detail.getCourt())
                .courtCaseNumber(detail.getCourtCaseNumber())
                .presidingJudge(detail.getPresidingJudge())
                .courtFilingDate(detail.getCourtFilingDate())
                .notes(detail.getNotes())
                .createdByName(detail.getCreatedBy().getFullName())
                .createdByEmail(detail.getCreatedBy().getEmail())
                .createdAt(detail.getCreatedAt())
                .lastUpdatedByName(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getFullName() : null)
                .lastUpdatedByEmail(detail.getLastUpdatedBy() != null ? detail.getLastUpdatedBy().getEmail() : null)
                .lastUpdatedAt(detail.getLastUpdatedAt())
                .charges(charges)
                .totalCharges(charges.size())
                .pendingCharges(
                        chargeRepository.countByCriminalCaseDetailIdAndStatus(detail.getId(), ChargeStatus.PENDING))
                .convictedCharges(
                        chargeRepository.countByCriminalCaseDetailIdAndStatus(detail.getId(), ChargeStatus.CONVICTED))
                .acquittedCharges(
                        chargeRepository.countByCriminalCaseDetailIdAndStatus(detail.getId(), ChargeStatus.ACQUITTED))
                .withdrawnCharges(
                        chargeRepository.countByCriminalCaseDetailIdAndStatus(detail.getId(), ChargeStatus.WITHDRAWN))
                .hearings(hearings)
                .nextHearingDate(latestAdjourned != null ? latestAdjourned.getNextHearingDate() : null)
                .nextHearingPurpose(latestAdjourned != null ? latestAdjourned.getNextHearingPurpose() : null)
                .build();
    }

    // ─── Header
    // ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CriminalCaseDetailResponse setCaseDetails(Long caseId, CriminalCaseDetailRequest request) {
        InitialCaseEntity caseEntity = findAndValidateCase(caseId);
        UserEntity actor = currentUser();
        CriminalCaseDetail detail;

        if (detailRepository.existsByInitialCaseId(caseId)) {
            detail = findDetail(caseId);
            detail.setAccusedName(request.getAccusedName());
            detail.setAccusedIdNumber(request.getAccusedIdNumber());
            detail.setAccusedAddress(request.getAccusedAddress());
            detail.setCourt(request.getCourt());
            detail.setCourtCaseNumber(request.getCourtCaseNumber());
            detail.setPresidingJudge(request.getPresidingJudge());
            detail.setCourtFilingDate(request.getCourtFilingDate());
            detail.setNotes(request.getNotes());
            detail.setLastUpdatedBy(actor);
            detail.setLastUpdatedAt(LocalDateTime.now());
        } else {
            detail = CriminalCaseDetail.builder()
                    .initialCase(caseEntity)
                    .accusedName(request.getAccusedName())
                    .accusedIdNumber(request.getAccusedIdNumber())
                    .accusedAddress(request.getAccusedAddress())
                    .court(request.getCourt())
                    .courtCaseNumber(request.getCourtCaseNumber())
                    .presidingJudge(request.getPresidingJudge())
                    .courtFilingDate(request.getCourtFilingDate())
                    .notes(request.getNotes())
                    .createdBy(actor)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDetailResponse(detailRepository.save(detail));
    }

    @Override
    @Transactional(readOnly = true)
    public CriminalCaseDetailResponse getCaseDetail(Long caseId) {
        findAndValidateCase(caseId);
        return toDetailResponse(findDetail(caseId));
    }

    // ─── Feature 1: Charge Management ────────────────────────────────────────────

    @Override
    @Transactional
    public CriminalChargeResponse addCharge(Long caseId, CriminalChargeRequest request) {
        findAndValidateCase(caseId);
        CriminalCaseDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        CriminalCharge charge = CriminalCharge.builder()
                .criminalCaseDetail(detail)
                .statute(request.getStatute())
                .section(request.getSection())
                .offenceName(request.getOffenceName())
                .offenceDescription(request.getOffenceDescription())
                .maximumPenalty(request.getMaximumPenalty())
                .plea(request.getPlea() != null ? request.getPlea() : PleaType.NO_PLEA_ENTERED)
                .status(request.getStatus() != null ? request.getStatus() : ChargeStatus.PENDING)
                .outcomeDetails(request.getOutcomeDetails())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toChargeResponse(chargeRepository.save(charge));
    }

    @Override
    @Transactional
    public CriminalChargeResponse updateCharge(Long caseId, Long chargeId, CriminalChargeRequest request) {
        findAndValidateCase(caseId);
        CriminalCharge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new IllegalArgumentException("Charge not found with id: " + chargeId));
        assertChargeOwnership(charge, caseId);

        UserEntity actor = currentUser();
        charge.setStatute(request.getStatute());
        charge.setSection(request.getSection());
        charge.setOffenceName(request.getOffenceName());
        charge.setOffenceDescription(request.getOffenceDescription());
        charge.setMaximumPenalty(request.getMaximumPenalty());
        if (request.getPlea() != null)
            charge.setPlea(request.getPlea());
        if (request.getStatus() != null)
            charge.setStatus(request.getStatus());
        charge.setOutcomeDetails(request.getOutcomeDetails());
        charge.setNotes(request.getNotes());
        charge.setLastUpdatedBy(actor);
        charge.setLastUpdatedAt(LocalDateTime.now());

        return toChargeResponse(chargeRepository.save(charge));
    }

    @Override
    @Transactional
    public void deleteCharge(Long caseId, Long chargeId) {
        findAndValidateCase(caseId);
        CriminalCharge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new IllegalArgumentException("Charge not found with id: " + chargeId));
        assertChargeOwnership(charge, caseId);
        chargeRepository.delete(charge);
    }

    // ─── Feature 2: Hearing History
    // ───────────────────────────────────────────────

    @Override
    @Transactional
    public CourtHearingResponse addHearing(Long caseId, CourtHearingRequest request) {
        findAndValidateCase(caseId);
        CriminalCaseDetail detail = findDetail(caseId);
        UserEntity actor = currentUser();

        // Guard: ADJOURNED outcome must have a next hearing date
        if (request.getOutcome() == HearingOutcome.ADJOURNED && request.getNextHearingDate() == null) {
            throw new IllegalArgumentException(
                    "Next hearing date is required when outcome is ADJOURNED.");
        }

        CourtHearing hearing = CourtHearing.builder()
                .criminalCaseDetail(detail)
                .hearingDate(request.getHearingDate())
                .hearingType(request.getHearingType())
                .presidingJudge(request.getPresidingJudge())
                .proceedingsSummary(request.getProceedingsSummary())
                .outcome(request.getOutcome())
                .nextHearingDate(request.getNextHearingDate())
                .nextHearingPurpose(request.getNextHearingPurpose())
                .notes(request.getNotes())
                .recordedBy(actor)
                .recordedAt(LocalDateTime.now())
                .build();

        return toHearingResponse(hearingRepository.save(hearing));
    }

    @Override
    @Transactional
    public CourtHearingResponse updateHearing(Long caseId, Long hearingId, CourtHearingRequest request) {
        findAndValidateCase(caseId);
        CourtHearing hearing = hearingRepository.findById(hearingId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing not found with id: " + hearingId));
        assertHearingOwnership(hearing, caseId);

        if (request.getOutcome() == HearingOutcome.ADJOURNED && request.getNextHearingDate() == null) {
            throw new IllegalArgumentException("Next hearing date is required when outcome is ADJOURNED.");
        }

        UserEntity actor = currentUser();
        hearing.setHearingDate(request.getHearingDate());
        hearing.setHearingType(request.getHearingType());
        hearing.setPresidingJudge(request.getPresidingJudge());
        hearing.setProceedingsSummary(request.getProceedingsSummary());
        hearing.setOutcome(request.getOutcome());
        hearing.setNextHearingDate(request.getNextHearingDate());
        hearing.setNextHearingPurpose(request.getNextHearingPurpose());
        hearing.setNotes(request.getNotes());
        hearing.setLastUpdatedBy(actor);
        hearing.setLastUpdatedAt(LocalDateTime.now());

        return toHearingResponse(hearingRepository.save(hearing));
    }

    @Override
    @Transactional
    public void deleteHearing(Long caseId, Long hearingId) {
        findAndValidateCase(caseId);
        CourtHearing hearing = hearingRepository.findById(hearingId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing not found with id: " + hearingId));
        assertHearingOwnership(hearing, caseId);
        hearingRepository.delete(hearing);
    }
}
