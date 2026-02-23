package com.nipun.legalscale.feature.user;

import com.nipun.legalscale.feature.admin.dto.ChangeRoleRequest;
import com.nipun.legalscale.feature.admin.dto.UserDetailsResponse;

import java.util.List;

public interface UserService {
    List<UserDetailsResponse> getAllUsers();

    void banUser(String email);

    void unbanUser(String email);

    void changeUserRole(ChangeRoleRequest request);

    java.util.Map<String, Long> getRoleCounts();

    List<UserDetailsResponse> getAllOfficers();
}
