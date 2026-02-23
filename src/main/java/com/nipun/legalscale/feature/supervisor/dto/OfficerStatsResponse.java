package com.nipun.legalscale.feature.supervisor.dto;

import com.nipun.legalscale.feature.admin.dto.UserDetailsResponse;
import com.nipun.legalscale.feature.legalcasehandling.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerStatsResponse {
    private UserDetailsResponse officerDetails;
    private long totalAssignedCases;
    private Map<CaseStatus, Long> caseCountsByStatus;
}
