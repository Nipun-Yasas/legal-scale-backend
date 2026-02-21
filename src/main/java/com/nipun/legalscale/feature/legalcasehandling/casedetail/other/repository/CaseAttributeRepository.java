package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity.CaseAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseAttributeRepository extends JpaRepository<CaseAttribute, Long> {

    /** All attributes ordered by category then displayOrder */
    List<CaseAttribute> findByOtherCaseDetailIdOrderByCategoryAscDisplayOrderAscAttributeNameAsc(Long detailId);

    /**
     * Check for duplicate attribute name within a case (excluding current record on
     * update)
     */
    boolean existsByOtherCaseDetailIdAndAttributeNameAndIdNot(
            Long detailId, String attributeName, Long excludeId);

    /** Check for existence (used on create) */
    boolean existsByOtherCaseDetailIdAndAttributeName(Long detailId, String attributeName);

    Optional<CaseAttribute> findByOtherCaseDetailIdAndAttributeName(Long detailId, String attributeName);
}
