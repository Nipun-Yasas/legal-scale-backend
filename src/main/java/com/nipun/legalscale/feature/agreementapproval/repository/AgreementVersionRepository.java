package com.nipun.legalscale.feature.agreementapproval.repository;

import com.nipun.legalscale.feature.agreementapproval.entity.AgreementVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementVersionRepository extends JpaRepository<AgreementVersionEntity, Long> {
    List<AgreementVersionEntity> findByAgreementIdOrderByVersionNumberDesc(Long agreementId);
}
