package com.nipun.legalscale.feature.user;

import com.nipun.legalscale.feature.admin.dto.ChangeRoleRequest;
import com.nipun.legalscale.feature.admin.dto.UserDetailsResponse;
import com.nipun.legalscale.feature.auth.enums.Role;
import com.nipun.legalscale.feature.user.entity.RoleEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.RoleRepository;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;

        @Override
        public List<UserDetailsResponse> getAllUsers() {
                return userRepository.findAll().stream()
                                .filter(user -> user.getRole() == null
                                                || user.getRole().getRoleName() != Role.SYSTEM_ADMIN)
                                .map(user -> UserDetailsResponse.builder()
                                                .id(user.getId())
                                                .fullName(user.getFullName())
                                                .email(user.getEmail())
                                                .isBanned(user.isBanned())
                                                .roleId(user.getRole().getId())
                                                .roleName(user.getRole().getRoleName())
                                                .legalDepartmentMember(user.getRole().isLegalDepartmentMember())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<UserDetailsResponse> getAllOfficers() {
                return userRepository.findAll().stream()
                                .filter(user -> user.getRole() != null
                                                && user.getRole().getRoleName() == Role.LEGAL_OFFICER)
                                .map(user -> UserDetailsResponse.builder()
                                                .id(user.getId())
                                                .fullName(user.getFullName())
                                                .email(user.getEmail())
                                                .isBanned(user.isBanned())
                                                .roleId(user.getRole().getId())
                                                .roleName(user.getRole().getRoleName())
                                                .legalDepartmentMember(user.getRole().isLegalDepartmentMember())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public void banUser(String email) {
                UserEntity user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
                user.setBanned(true);
                userRepository.save(user);
        }

        @Override
        public void unbanUser(String email) {
                UserEntity user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
                user.setBanned(false);
                userRepository.save(user);
        }

        @Override
        public void changeUserRole(ChangeRoleRequest request) {
                UserEntity user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found: " + request.getEmail()));

                if (request.getNewRole() != null) {
                        boolean isLegalDeptMember = isLegalDepartmentRole(request.getNewRole());

                        RoleEntity role = roleRepository.findByRoleName(request.getNewRole())
                                        .orElseGet(() -> roleRepository.save(
                                                        RoleEntity.builder()
                                                                        .roleName(request.getNewRole())
                                                                        .legalDepartmentMember(isLegalDeptMember)
                                                                        .build()));

                        user.setRole(role);
                }

                if (request.getApproverLevel() != null) {
                        user.setApproverLevel(request.getApproverLevel());
                }

                userRepository.save(user);
        }

        private boolean isLegalDepartmentRole(Role role) {
                return switch (role) {
                        case LEGAL_OFFICER, LEGAL_SUPERVISOR, AGREEMENT_REVIEWER, AGREEMENT_APPROVER -> true;
                        default -> false;
                };
        }

        @Override
        public java.util.Map<String, Object> getRoleCounts() {
                List<UserEntity> allUsers = userRepository.findAll();

                java.util.Map<String, Object> response = new java.util.HashMap<>();

                response.put("LEGAL_OFFICER",
                                allUsers.stream()
                                                .filter(u -> u.getRole() != null
                                                                && u.getRole().getRoleName() == Role.LEGAL_OFFICER)
                                                .count());
                response.put("LEGAL_SUPERVISOR",
                                allUsers.stream()
                                                .filter(u -> u.getRole() != null
                                                                && u.getRole().getRoleName() == Role.LEGAL_SUPERVISOR)
                                                .count());
                response.put("AGREEMENT_REVIEWER",
                                allUsers.stream()
                                                .filter(u -> u.getRole() != null
                                                                && u.getRole().getRoleName() == Role.AGREEMENT_REVIEWER)
                                                .count());
                response.put("AGREEMENT_APPROVER",
                                allUsers.stream()
                                                .filter(u -> u.getRole() != null
                                                                && u.getRole().getRoleName() == Role.AGREEMENT_APPROVER)
                                                .count());
                response.put("MANAGEMENT",
                                allUsers.stream()
                                                .filter(u -> u.getRole() != null
                                                                && u.getRole().getRoleName() == Role.MANAGEMENT)
                                                .count());
                response.put("USER",
                                allUsers.stream().filter(
                                                u -> u.getRole() != null && u.getRole().getRoleName() == Role.USER)
                                                .count());

                java.util.Map<Integer, Long> approverLevelCounts = allUsers.stream()
                                .filter(u -> u.getApproverLevel() != null)
                                .collect(Collectors.groupingBy(UserEntity::getApproverLevel, Collectors.counting()));

                response.put("approverLevels", approverLevelCounts);

                return response;
        }
}
