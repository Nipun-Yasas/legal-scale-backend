package com.nipun.legalscale.feature.auth.dto;

import com.nipun.legalscale.feature.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Role role;
    private boolean isMemberOfLegalDepartment;
}
