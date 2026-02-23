package com.nipun.legalscale.feature.agreementapproval.service;

import com.nipun.legalscale.feature.agreementapproval.dto.AgreementResponse;
import com.nipun.legalscale.feature.agreementapproval.dto.CreateAgreementRequest;
import com.nipun.legalscale.feature.agreementapproval.dto.ReviewAgreementRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AgreementService {

    // User functions
    AgreementResponse createAgreement(CreateAgreementRequest request, MultipartFile document);

    AgreementResponse uploadRevision(Long agreementId, String revisionNotes, MultipartFile document);

    AgreementResponse requestReview(Long agreementId, ReviewAgreementRequest request);

    List<AgreementResponse> getMyAgreements();

    AgreementResponse getAgreementById(Long id);

    // Reviewer functions
    List<AgreementResponse> getAgreementsForReview();

    AgreementResponse reviewAgreement(Long agreementId, ReviewAgreementRequest request);

    AgreementResponse respondToRevision(Long agreementId, ReviewAgreementRequest request);

    AgreementResponse addComment(Long agreementId, String commentText);

    // Approver functions
    List<AgreementResponse> getAgreementsForApproval();

    AgreementResponse approveOrReject(Long agreementId, ReviewAgreementRequest request);

    AgreementResponse executeAgreement(Long agreementId);

    AgreementResponse digitallySignAgreement(Long agreementId);

    // General
    List<AgreementResponse> getAllAgreements();

    java.util.Map<String, Long> getAgreementStatusCounts();
}
