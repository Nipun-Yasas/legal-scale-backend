package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity.SettlementAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementAgreementRepository extends JpaRepository<SettlementAgreement, Long> {

    Optional<SettlementAgreement> findByDamagesRecoveryDetailId(Long detailId);

    boolean existsByDamagesRecoveryDetailId(Long detailId);
}
