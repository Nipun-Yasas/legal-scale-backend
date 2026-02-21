package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity.InquiryDecision;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.DecisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryDecisionRepository extends JpaRepository<InquiryDecision, Long> {

    List<InquiryDecision> findByInquiryDetailIdOrderByRecordedAtAsc(Long inquiryDetailId);

    long countByInquiryDetailIdAndStatus(Long inquiryDetailId, DecisionStatus status);
}
