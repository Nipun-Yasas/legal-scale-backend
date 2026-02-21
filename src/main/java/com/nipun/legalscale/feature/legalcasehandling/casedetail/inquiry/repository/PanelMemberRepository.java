package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.entity.PanelMember;
import com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums.PanelMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PanelMemberRepository extends JpaRepository<PanelMember, Long> {

    List<PanelMember> findByInquiryDetailIdOrderByRoleAscMemberNameAsc(Long inquiryDetailId);

    /** Validate: only one CHAIRPERSON allowed per panel */
    boolean existsByInquiryDetailIdAndRole(Long inquiryDetailId, PanelMemberRole role);
}
