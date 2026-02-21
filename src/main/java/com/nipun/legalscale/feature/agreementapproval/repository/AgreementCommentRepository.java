package com.nipun.legalscale.feature.agreementapproval.repository;

import com.nipun.legalscale.feature.agreementapproval.entity.AgreementCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementCommentRepository extends JpaRepository<AgreementCommentEntity, Long> {
    List<AgreementCommentEntity> findByAgreementIdOrderByCreatedAtDesc(Long agreementId);
}
