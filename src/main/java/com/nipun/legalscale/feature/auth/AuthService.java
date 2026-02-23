package com.nipun.legalscale.feature.auth;

import com.nipun.legalscale.feature.auth.dto.AuthResponse;
import com.nipun.legalscale.feature.auth.dto.LoginRequest;
import com.nipun.legalscale.feature.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse getCurrentUser();
}
