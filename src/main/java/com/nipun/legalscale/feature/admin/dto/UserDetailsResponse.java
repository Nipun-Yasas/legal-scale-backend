package com.nipun.legalscale.feature.admin.dto;

import com.nipun.legalscale.feature.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private Long id;
    private String fullName;
    private String email;
    private boolean isBanned;
    private Long roleId;
    private Role roleName;
    private boolean legalDepartmentMember;
}
