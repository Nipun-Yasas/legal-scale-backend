package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity.InquiryFinding;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.FindingSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryFindingRepository extends JpaRepository<InquiryFinding, Long> {

    List<InquiryFinding> findByInquiryDetailIdOrderByFindingNumberAsc(Long inquiryDetailId);

    long countByInquiryDetailIdAndSeverity(Long inquiryDetailId, FindingSeverity severity);

    /** Get the highest finding number so far, to auto-increment for new findings */
    @Query("SELECT COALESCE(MAX(f.findingNumber), 0) FROM InquiryFinding f " +
            "WHERE f.inquiryDetail.id = :detailId")
    Integer findMaxFindingNumber(@Param("detailId") Long detailId);
}
