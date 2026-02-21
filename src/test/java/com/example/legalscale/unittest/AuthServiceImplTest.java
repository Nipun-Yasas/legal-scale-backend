package com.example.legalscale.unittest;

import com.nipun.legalscale.feature.auth.enums.Role;
import com.nipun.legalscale.core.security.JwtService;
import com.nipun.legalscale.feature.auth.AuthServiceImpl;
import com.nipun.legalscale.feature.auth.dto.AuthResponse;
import com.nipun.legalscale.feature.auth.dto.LoginRequest;
import com.nipun.legalscale.feature.auth.dto.RegisterRequest;
import com.nipun.legalscale.feature.user.entity.RoleEntity;
import com.nipun.legalscale.feature.user.entity.UserEntity;
import com.nipun.legalscale.feature.user.repository.RoleRepository;
import com.nipun.legalscale.feature.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RoleEntity legalOfficerRole;
    private RoleEntity userRole;
    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        legalOfficerRole = RoleEntity.builder()
                .id(1L)
                .roleName(Role.LEGAL_OFFICER)
                .legalDepartmentMember(true)
                .build();

        userRole = RoleEntity.builder()
                .id(2L)
                .roleName(Role.USER)
                .legalDepartmentMember(false)
                .build();

        savedUser = UserEntity.builder()
                .id(1L)
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("encodedPassword")
                .role(userRole)
                .build();
    }

    // ─── register() ───────────────────────────────────────────────────────────

    @Test
    void register_withNewEmail_shouldReturnAuthResponse() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("secret123")
                .roleName(Role.USER)
                .build();

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(roleRepository.findByRoleName(Role.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getFullName()).isEqualTo("Jane Doe");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.isMemberOfLegalDepartment()).isFalse();

        verify(userRepository).existsByEmail("jane@example.com");
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtService).generateToken(any(UserEntity.class));
    }

    @Test
    void register_withLegalDepartmentRole_shouldSetLegalDeptMemberTrue() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("John Legal")
                .email("john@legal.com")
                .password("pass456")
                .roleName(Role.LEGAL_OFFICER)
                .build();

        UserEntity legalUser = UserEntity.builder()
                .id(2L)
                .fullName("John Legal")
                .email("john@legal.com")
                .password("encodedPass")
                .role(legalOfficerRole)
                .build();

        when(userRepository.existsByEmail("john@legal.com")).thenReturn(false);
        when(roleRepository.findByRoleName(Role.LEGAL_OFFICER)).thenReturn(Optional.of(legalOfficerRole));
        when(passwordEncoder.encode("pass456")).thenReturn("encodedPass");
        when(userRepository.save(any(UserEntity.class))).thenReturn(legalUser);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("legal-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.isMemberOfLegalDepartment()).isTrue();
        assertThat(response.getRole()).isEqualTo(Role.LEGAL_OFFICER);
    }

    @Test
    void register_withExistingEmail_shouldThrowIllegalArgumentException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("secret123")
                .roleName(Role.USER)
                .build();

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void register_whenRoleNotFound_shouldCreateAndSaveNewRole() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("New Admin")
                .email("admin@example.com")
                .password("adminPass")
                .roleName(Role.SYSTEM_ADMIN)
                .build();

        RoleEntity newRole = RoleEntity.builder()
                .roleName(Role.SYSTEM_ADMIN)
                .legalDepartmentMember(false)
                .build();

        UserEntity adminUser = UserEntity.builder()
                .id(3L)
                .fullName("New Admin")
                .email("admin@example.com")
                .password("encodedAdminPass")
                .role(newRole)
                .build();

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(roleRepository.findByRoleName(Role.SYSTEM_ADMIN)).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(newRole);
        when(passwordEncoder.encode("adminPass")).thenReturn("encodedAdminPass");
        when(userRepository.save(any(UserEntity.class))).thenReturn(adminUser);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("admin-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("admin-jwt-token");
        verify(roleRepository).save(any(RoleEntity.class));
    }

    // ─── login() ──────────────────────────────────────────────────────────────

    @Test
    void login_withValidCredentials_shouldReturnAuthResponse() {
        LoginRequest request = LoginRequest.builder()
                .email("jane@example.com")
                .password("secret123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getFullName()).isEqualTo("Jane Doe");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.isMemberOfLegalDepartment()).isFalse();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("jane@example.com");
        verify(jwtService).generateToken(savedUser);
    }

    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        LoginRequest request = LoginRequest.builder()
                .email("jane@example.com")
                .password("wrongPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_whenUserNotFoundAfterAuthentication_shouldThrowIllegalArgumentException() {
        LoginRequest request = LoginRequest.builder()
                .email("ghost@example.com")
                .password("pass")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(jwtService, never()).generateToken(any());
    }
}
