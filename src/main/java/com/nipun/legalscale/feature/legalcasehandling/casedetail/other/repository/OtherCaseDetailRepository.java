package com.nipun.legalscale.feature.legalcasehandling.casedetail.other.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.other.entity.OtherCaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtherCaseDetailRepository extends JpaRepository<OtherCaseDetail, Long> {

    Optional<OtherCaseDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
