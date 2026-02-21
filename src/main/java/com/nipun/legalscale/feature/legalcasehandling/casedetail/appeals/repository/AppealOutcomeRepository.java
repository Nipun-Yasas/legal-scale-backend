package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity.AppealOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppealOutcomeRepository extends JpaRepository<AppealOutcome, Long> {

    Optional<AppealOutcome> findByAppealDetailId(Long appealDetailId);

    boolean existsByAppealDetailId(Long appealDetailId);
}
