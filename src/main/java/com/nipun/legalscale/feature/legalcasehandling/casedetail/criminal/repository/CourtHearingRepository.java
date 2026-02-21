package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.entity.CourtHearing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtHearingRepository extends JpaRepository<CourtHearing, Long> {

    /** Full hearing history in chronological order (earliest first) */
    List<CourtHearing> findByCriminalCaseDetailIdOrderByHearingDateAsc(Long detailId);
}
