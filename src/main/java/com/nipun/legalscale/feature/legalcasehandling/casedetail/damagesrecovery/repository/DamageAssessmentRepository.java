package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity.DamageAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DamageAssessmentRepository extends JpaRepository<DamageAssessment, Long> {

    List<DamageAssessment> findByDamagesRecoveryDetailIdOrderByAssessmentDateAsc(Long detailId);

    /**
     * Total estimated value of all assessments for a given detail.
     * Used to calculate the aggregate assessed value.
     */
    @Query("SELECT COALESCE(SUM(a.estimatedValue), 0) FROM DamageAssessment a " +
            "WHERE a.damagesRecoveryDetail.id = :detailId")
    BigDecimal sumEstimatedValueByDetailId(@Param("detailId") Long detailId);
}
