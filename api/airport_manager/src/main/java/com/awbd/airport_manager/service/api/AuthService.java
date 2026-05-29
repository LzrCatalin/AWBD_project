package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.AuthResponse;
import com.awbd.airport_manager.dto.LoginRequest;
import com.awbd.airport_manager.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
