package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity.DamagesRecoveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DamagesRecoveryDetailRepository extends JpaRepository<DamagesRecoveryDetail, Long> {

    Optional<DamagesRecoveryDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
