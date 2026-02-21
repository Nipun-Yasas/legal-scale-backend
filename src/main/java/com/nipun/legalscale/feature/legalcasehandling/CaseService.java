package com.nipun.legalscale.feature.legalcasehandling;

import com.nipun.legalscale.feature.legalcasehandling.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseService {

    // ─── Case Creation (any authenticated user) ──────────────────────────────────

    CaseResponse createCase(CreateCaseRequest request, List<MultipartFile> attachments);

    // ─── Read
    // ─────────────────────────────────────────────────────────────────────

    List<CaseResponse> getAllNewCases();

    List<CaseResponse> getAllCases();

    CaseResponse getCaseById(Long id);

    List<CaseResponse> getCasesAssignedToCurrentOfficer();

    // ─── Supervisor Actions
    // ───────────────────────────────────────────────────────

    CaseResponse assignCaseToOfficer(Long caseId, AssignCaseRequest request);

    CaseResponse supervisorUpdateStatus(Long caseId, UpdateCaseStatusRequest request);

    CaseCommentResponse supervisorAddComment(Long caseId, AddCommentRequest request);

    // ─── Officer Actions
    // ──────────────────────────────────────────────────────────

    CaseResponse officerUpdateStatus(Long caseId, UpdateCaseStatusRequest request);

    CaseCommentResponse officerAddComment(Long caseId, AddCommentRequest request);

    // ─── Attachment Management (available to case creators, officers, supervisors)
    // ─────────

    /**
     * Upload a file and attach it to the given case as a supporting attachment.
     * Returns the updated case.
     */
    CaseResponse attachDocument(Long caseId, MultipartFile file);

    /**
     * Remove a previously attached document from the case (does not delete the
     * document itself).
     */
    CaseResponse removeAttachment(Long caseId, Long documentId);
}
