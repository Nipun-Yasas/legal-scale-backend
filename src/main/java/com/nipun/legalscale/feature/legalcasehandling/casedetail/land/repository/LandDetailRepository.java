package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity.LandDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LandDetailRepository extends JpaRepository<LandDetail, Long> {

    Optional<LandDetail> findByInitialCaseId(Long caseId);

    boolean existsByInitialCaseId(Long caseId);

    /** Check that a land reference number is not already in use by another case */
    boolean existsByLandReferenceNumberAndInitialCaseIdNot(String landReferenceNumber, Long caseId);
}
