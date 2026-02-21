package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.entity.AppealDeadline;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums.DeadlineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppealDeadlineRepository extends JpaRepository<AppealDeadline, Long> {

    /** All deadlines for a given appeal, ordered by soonest date first */
    List<AppealDeadline> findByAppealDetailIdOrderByDeadlineDateAsc(Long appealDetailId);

    /** Upcoming deadlines with PENDING status, useful for dashboard alerts */
    List<AppealDeadline> findByAppealDetailIdAndStatusAndDeadlineDateBefore(
            Long appealDetailId, DeadlineStatus status, LocalDate before);
}
