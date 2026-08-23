package com.decisify.auth.service;

import com.decisify.auth.domain.User;
import com.decisify.auth.dto.RegisterRequest;
import com.decisify.auth.dto.RegisterResponse;
import com.decisify.auth.dto.UserProfileResponse;
import com.decisify.auth.exception.EmailAlreadyExistsException;
import com.decisify.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.decisify.auth.dto.LoginRequest;
import com.decisify.auth.dto.LoginResponse;
import com.decisify.auth.dto.RefreshTokenRequest;
import com.decisify.auth.dto.RefreshTokenResponse;
import com.decisify.auth.exception.InvalidCredentialsException;
import com.decisify.auth.exception.InvalidRefreshTokenException;
import com.decisify.auth.exception.UserNotFoundException;
import com.decisify.auth.security.JwtService;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User(
                UUID.randomUUID(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                OffsetDateTime.now()
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        UUID userId = jwtService.validateRefreshTokenAndGetUserId(
                request.refreshToken()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        String accessToken = jwtService.generateAccessToken(user);

        return new RefreshTokenResponse(
                accessToken,
                "Bearer"
        );
    }

    @Override
    public UserProfileResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCreatedAt()
        );
    }
}