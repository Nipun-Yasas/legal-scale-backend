package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity.LandDeedPlan;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums.LandDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandDeedPlanRepository extends JpaRepository<LandDeedPlan, Long> {

    List<LandDeedPlan> findByLandDetailIdOrderByIssueDateDesc(Long landDetailId);

    /** Filter deeds/plans by document type */
    List<LandDeedPlan> findByLandDetailIdAndDocumentTypeOrderByIssueDateDesc(
            Long landDetailId, LandDocumentType documentType);
}
