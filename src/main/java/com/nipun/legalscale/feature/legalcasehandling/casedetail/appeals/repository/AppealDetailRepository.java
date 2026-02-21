package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity.AppealDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppealDetailRepository extends JpaRepository<AppealDetail, Long> {

    Optional<AppealDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
