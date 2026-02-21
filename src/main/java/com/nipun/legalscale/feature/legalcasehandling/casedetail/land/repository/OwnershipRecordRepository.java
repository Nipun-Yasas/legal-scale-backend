package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.land.entity.OwnershipRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnershipRecordRepository extends JpaRepository<OwnershipRecord, Long> {

    /**
     * All ownership records ordered from earliest to latest — shows full ownership
     * chain
     */
    List<OwnershipRecord> findByLandDetailIdOrderByOwnershipStartDateAsc(Long landDetailId);
}
