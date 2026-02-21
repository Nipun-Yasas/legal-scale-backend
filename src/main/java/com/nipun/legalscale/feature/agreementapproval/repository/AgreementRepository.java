package com.nipun.legalscale.feature.agreementapproval.repository;

import com.nipun.legalscale.feature.agreementapproval.entity.AgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository
        extends JpaRepository<AgreementEntity, Long>, JpaSpecificationExecutor<AgreementEntity> {
    List<AgreementEntity> findByCreatedById(Long userId);

    List<AgreementEntity> findByReviewerId(Long reviewerId);

    List<AgreementEntity> findByApproverId(Long approverId);
}
