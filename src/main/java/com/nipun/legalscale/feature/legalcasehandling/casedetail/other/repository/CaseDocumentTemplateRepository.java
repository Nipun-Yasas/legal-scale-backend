package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity.CaseDocumentTemplate;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.enums.TemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseDocumentTemplateRepository extends JpaRepository<CaseDocumentTemplate, Long> {

    /** All templates, most recently created first */
    List<CaseDocumentTemplate> findByOtherCaseDetailIdOrderByCreatedAtDesc(Long detailId);

    /** Only ACTIVE templates */
    List<CaseDocumentTemplate> findByOtherCaseDetailIdAndStatusOrderByTemplateNameAsc(
            Long detailId, TemplateStatus status);

    /** Count of ACTIVE templates per case */
    long countByOtherCaseDetailIdAndStatus(Long detailId, TemplateStatus status);
}
