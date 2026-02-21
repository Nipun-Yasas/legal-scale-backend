package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity.MoneyRecoveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyRecoveryDetailRepository extends JpaRepository<MoneyRecoveryDetail, Long> {

    Optional<MoneyRecoveryDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);
}
