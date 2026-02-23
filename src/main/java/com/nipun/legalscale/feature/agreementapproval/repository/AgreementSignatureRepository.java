package com.nipun.legalscale.feature.agreementapproval.repository;

import com.nipun.legalscale.feature.agreementapproval.entity.AgreementSignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementSignatureRepository extends JpaRepository<AgreementSignatureEntity, Long> {
}
