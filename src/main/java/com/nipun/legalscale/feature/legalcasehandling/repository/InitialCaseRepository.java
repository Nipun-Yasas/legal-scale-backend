package com.nipun.legalscale.feature.legalcasehandling.repository;

import com.nipun.legalscale.feature.legalcasehandling.entity.InitialCaseEntity;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InitialCaseRepository extends JpaRepository<InitialCaseEntity, Long> {

    List<InitialCaseEntity> findByStatus(CaseStatus status);

    List<InitialCaseEntity> findByStatusAndAssignedOfficerIsNull(CaseStatus status);

    List<InitialCaseEntity> findByAssignedOfficerId(Long officerId);

    List<InitialCaseEntity> findByCreatedSupervisorId(Long supervisorId);

    boolean existsByReferenceNumber(String referenceNumber);
}
