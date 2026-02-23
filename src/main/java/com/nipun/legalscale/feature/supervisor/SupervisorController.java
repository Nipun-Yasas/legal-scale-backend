package com.nipun.legalscale.feature.supervisor;

import com.nipun.legalscale.feature.legalcasehandling.CaseService;
import com.nipun.legalscale.feature.legalcasehandling.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import com.nipun.legalscale.feature.user.UserService;
import com.nipun.legalscale.feature.admin.dto.UserDetailsResponse;
import com.nipun.legalscale.feature.legalcasehandling.repository.InitialCaseRepository;
import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import com.nipun.legalscale.feature.supervisor.dto.OfficerStatsResponse;
import com.nipun.legalscale.feature.supervisor.dto.CaseActivityResponse;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Supervisor-only endpoints.
 * Role guard: LEGAL_SUPERVISOR
 */
@RestController
@RequestMapping("/api/supervisor/cases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LEGAL_SUPERVISOR')")
public class SupervisorController {

    private final CaseService caseService;
    private final UserService userService;
    private final InitialCaseRepository initialCaseRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * POST /api/supervisor/cases
     *
     * Creates a new case and optionally attaches supporting documents in one shot.
     *
     * Content-Type: multipart/form-data
     */
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<CaseResponse> createCase(
            @RequestParam("data") String dataJson,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments) {

        // Deserialize the JSON string manually
        CreateCaseRequest request;
        try {
            request = objectMapper.readValue(dataJson, CreateCaseRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in 'data' field: " + e.getOriginalMessage());
        }

        // Run Bean Validation manually
        Set<ConstraintViolation<CreateCaseRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (attachments == null) {
            attachments = Collections.emptyList();
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(caseService.createCase(request, attachments));
    }

    /**
     * GET /api/supervisor/cases/new
     * View all cases with status NEW that are awaiting review.
     */
    @GetMapping("/new")
    public ResponseEntity<List<CaseResponse>> getNewCases() {
        return ResponseEntity.ok(caseService.getAllNewCases());
    }

    /**
     * GET /api/supervisor/cases/my
     * View all cases created by the current supervisor.
     */
    @GetMapping("/my")
    public ResponseEntity<List<CaseResponse>> getMyCases() {
        return ResponseEntity.ok(caseService.getCasesCreatedByCurrentSupervisor());
    }

    /**
     * GET /api/supervisor/cases/activities
     * Aggregates comments and remarks for cases created by the current supervisor.
     */
    @GetMapping("/activities")
    public ResponseEntity<List<CaseActivityResponse>> getMyCaseActivities() {
        List<CaseResponse> cases = caseService.getCasesCreatedByCurrentSupervisor();
        List<CaseActivityResponse> activities = new ArrayList<>();

        for (CaseResponse c : cases) {
            // Process Comments
            if (c.getComments() != null) {
                for (CaseCommentResponse comment : c.getComments()) {
                    activities.add(CaseActivityResponse.builder()
                            .type("COMMENT")
                            .caseId(c.getId())
                            .caseTitle(c.getCaseTitle())
                            .referenceNumber(c.getReferenceNumber())
                            .authorName(comment.getCommentedByName())
                            .authorEmail(comment.getCommentedByEmail())
                            .content(comment.getComment())
                            .timestamp(comment.getCommentedAt())
                            .build());
                }
            }

            // Process Closing Remarks
            if (c.getClosingRemarks() != null && !c.getClosingRemarks().isBlank()) {
                activities.add(CaseActivityResponse.builder()
                        .type("CLOSING_REMARK")
                        .caseId(c.getId())
                        .caseTitle(c.getCaseTitle())
                        .referenceNumber(c.getReferenceNumber())
                        .authorName(c.getClosedByName())
                        .authorEmail(c.getClosedByEmail())
                        .content(c.getClosingRemarks())
                        .timestamp(c.getClosedAt())
                        .build());
            }
        }

        // Sort dynamically using Java 8 comparators (lambda expression) to avoid syntax
        // restrictions
        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        return ResponseEntity.ok(activities);
    }

    /**
     * GET /api/supervisor/cases/officers
     * Retrieve a list of all legal officers along with assigned case stats.
     */
    @GetMapping("/officers")
    public ResponseEntity<List<OfficerStatsResponse>> getAllOfficersWithStats() {
        List<UserDetailsResponse> officers = userService.getAllOfficers();

        List<OfficerStatsResponse> response = officers.stream().map(officer -> {
            List<InitialCaseEntity> assignedCases = initialCaseRepository.findByAssignedOfficerId(officer.getId());

            Map<CaseStatus, Long> countsByStatus = assignedCases.stream()
                    .collect(Collectors.groupingBy(InitialCaseEntity::getStatus, Collectors.counting()));

            return OfficerStatsResponse.builder()
                    .officerDetails(officer)
                    .totalAssignedCases(assignedCases.size())
                    .caseCountsByStatus(countsByStatus)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/supervisor/cases
     * View all cases regardless of status.
     */
    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    /**
     * GET /api/supervisor/cases/{id}
     * View details of a specific case.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    /**
     * POST /api/supervisor/cases/{id}/assign
     * Assign a case to a Legal Officer.
     * This records the supervisor, the assigned officer, and the timestamp.
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<CaseResponse> assignCase(
            @PathVariable Long id,
            @Valid @RequestBody AssignCaseRequest request) {
        return ResponseEntity.ok(caseService.assignCaseToOfficer(id, request));
    }

    /**
     * PATCH /api/supervisor/cases/{id}/status
     * Change the status of a case.
     * When approving (ACTIVE), approvedBy / approvedAt are recorded.
     * When closing (CLOSED), closingRemarks are mandatory, closedBy / closedAt are
     * recorded.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CaseResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request) {
        return ResponseEntity.ok(caseService.supervisorUpdateStatus(id, request));
    }

    /**
     * POST /api/supervisor/cases/{id}/comments
     * Add a comment to a case.
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CaseCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(caseService.supervisorAddComment(id, request));
    }
}
