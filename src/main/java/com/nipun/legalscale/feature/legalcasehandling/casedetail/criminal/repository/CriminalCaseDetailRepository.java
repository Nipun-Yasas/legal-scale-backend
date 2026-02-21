package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity.CriminalCaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CriminalCaseDetailRepository extends JpaRepository<CriminalCaseDetail, Long> {

    Optional<CriminalCaseDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
