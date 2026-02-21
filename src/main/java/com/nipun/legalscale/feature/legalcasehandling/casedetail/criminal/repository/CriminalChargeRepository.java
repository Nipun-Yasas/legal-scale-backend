package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity.CriminalCharge;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums.ChargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriminalChargeRepository extends JpaRepository<CriminalCharge, Long> {

    List<CriminalCharge> findByCriminalCaseDetailIdOrderByRecordedAtAsc(Long detailId);

    /** Count charges by status — useful for a case dashboard summary */
    long countByCriminalCaseDetailIdAndStatus(Long detailId, ChargeStatus status);
}
