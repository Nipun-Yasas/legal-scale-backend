package com.nipun.legalscale.feature.legalcasehandling.repository;

import com.nipun.legalscale.feature.legalcasehandling.entity.CaseCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseCommentRepository extends JpaRepository<CaseCommentEntity, Long> {

    List<CaseCommentEntity> findByInitialCaseIdOrderByCommentedAtAsc(Long caseId);
}
