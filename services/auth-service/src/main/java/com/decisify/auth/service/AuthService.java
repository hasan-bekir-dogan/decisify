package com.decisify.auth.service;

import com.decisify.auth.dto.LoginRequest;
import com.decisify.auth.dto.LoginResponse;
import com.decisify.auth.dto.RegisterRequest;
import com.decisify.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}