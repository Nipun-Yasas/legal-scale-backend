package com.nipun.legalscale.feature.admin.dto;

import com.nipun.legalscale.feature.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequest {
    private String email;
    private Role newRole;
}
