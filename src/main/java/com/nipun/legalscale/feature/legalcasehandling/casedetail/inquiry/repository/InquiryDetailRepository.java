package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity.InquiryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryDetailRepository extends JpaRepository<InquiryDetail, Long> {

    Optional<InquiryDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
