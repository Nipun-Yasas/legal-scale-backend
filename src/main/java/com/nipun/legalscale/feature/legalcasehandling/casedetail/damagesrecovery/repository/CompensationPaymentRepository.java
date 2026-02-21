package com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.damagesrecovery.entity.CompensationPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CompensationPaymentRepository extends JpaRepository<CompensationPayment, Long> {

    List<CompensationPayment> findByDamagesRecoveryDetailIdOrderByPaymentDateAsc(Long detailId);

    /**
     * Total compensation received so far for a given detail.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM CompensationPayment p " +
            "WHERE p.damagesRecoveryDetail.id = :detailId")
    BigDecimal sumAmountByDetailId(@Param("detailId") Long detailId);
}
