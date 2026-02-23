package com.nipun.legalscale.feature.auth;

import com.nipun.legalscale.core.exception.AccountBannedException;
import com.nipun.legalscale.core.security.JwtService;
import com.nipun.legalscale.feature.auth.dto.AuthResponse;
import com.nipun.legalscale.feature.auth.dto.LoginRequest;
import com.nipun.legalscale.feature.auth.dto.RegisterRequest;
import com.nipun.legalscale.feature.auth.enums.Role;
import com.nipun.legalscale.feature.user.entity.RoleEntity;
import com.nipun.legalscale.feature.user.repository.RoleRepository;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Override
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new IllegalArgumentException("Email already in use");
                }

                boolean isLegalDeptMember = isLegalDepartmentRole(request.getRoleName());

                RoleEntity role = roleRepository.findByRoleName(request.getRoleName())
                                .orElseGet(() -> roleRepository.save(
                                                RoleEntity.builder()
                                                                .roleName(request.getRoleName())
                                                                .legalDepartmentMember(isLegalDeptMember)
                                                                .build()));

                UserEntity user = UserEntity.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(role)
                                .build();

                userRepository.save(user);

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().getRoleName())
                                .isMemberOfLegalDepartment(user.getRole().isLegalDepartmentMember())
                                .build();
        }

        @Override
        public AuthResponse login(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(request.getEmail(),
                                                        request.getPassword()));
                } catch (DisabledException e) {
                        throw new AccountBannedException("Your account is banned from the system");
                } catch (org.springframework.security.authentication.BadCredentialsException e) {
                        throw new IllegalArgumentException("Email or password is incorrect");
                }

                UserEntity user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().getRoleName())
                                .isMemberOfLegalDepartment(user.getRole().isLegalDepartmentMember())
                                .build();
        }

        @Override
        public AuthResponse getCurrentUser() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                UserEntity user = userRepository.findByEmail(auth.getName())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                return AuthResponse.builder()
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().getRoleName())
                                .isMemberOfLegalDepartment(user.getRole().isLegalDepartmentMember())
                                .build();
        }

        private boolean isLegalDepartmentRole(Role role) {
                return switch (role) {
                        case LEGAL_OFFICER, LEGAL_SUPERVISOR, AGREEMENT_REVIEWER, AGREEMENT_APPROVER -> true;
                        default -> false;
                };
        }
}
