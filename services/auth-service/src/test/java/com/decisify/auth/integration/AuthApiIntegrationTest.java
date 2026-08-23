package com.decisify.auth.integration;

import com.decisify.auth.dto.LoginRequest;
import com.decisify.auth.dto.LoginResponse;
import com.decisify.auth.dto.RefreshTokenRequest;
import com.decisify.auth.dto.RefreshTokenResponse;
import com.decisify.auth.dto.RegisterRequest;
import com.decisify.auth.dto.RegisterResponse;
import com.decisify.auth.dto.UserProfileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "security.jwt.secret=12345678901234567890123456789012"
        }
)
@AutoConfigureTestRestTemplate
@Testcontainers
class AuthApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18.3");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterLoginRefreshAndGetCurrentUser() {

        RegisterRequest registerRequest = new RegisterRequest(
                "integration@example.com",
                "Password123!",
                "Integration User"
        );

        ResponseEntity<RegisterResponse> registerResponse =
                restTemplate.postForEntity(
                        "/auth/register",
                        registerRequest,
                        RegisterResponse.class
                );

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals(
                "integration@example.com",
                registerResponse.getBody().email()
        );

        LoginRequest loginRequest = new LoginRequest(
                "integration@example.com",
                "Password123!"
        );

        ResponseEntity<LoginResponse> loginResponse =
                restTemplate.postForEntity(
                        "/auth/login",
                        loginRequest,
                        LoginResponse.class
                );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().accessToken());
        assertNotNull(loginResponse.getBody().refreshToken());

        RefreshTokenRequest refreshRequest =
                new RefreshTokenRequest(
                        loginResponse.getBody().refreshToken()
                );

        ResponseEntity<RefreshTokenResponse> refreshResponse =
                restTemplate.postForEntity(
                        "/auth/refresh",
                        refreshRequest,
                        RefreshTokenResponse.class
                );

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody());
        assertNotNull(refreshResponse.getBody().accessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(
                refreshResponse.getBody().accessToken()
        );

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserProfileResponse> meResponse =
                restTemplate.exchange(
                        "/auth/me",
                        HttpMethod.GET,
                        entity,
                        UserProfileResponse.class
                );

        assertEquals(HttpStatus.OK, meResponse.getStatusCode());
        assertNotNull(meResponse.getBody());
        assertEquals(
                "integration@example.com",
                meResponse.getBody().email()
        );
        assertEquals(
                "Integration User",
                meResponse.getBody().fullName()
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {
        String email = "duplicate@example.com";

        Map<String, String> request = Map.of(
                        "email", email,
                        "password", "Password123!",
                        "fullName", "Test User");

        ResponseEntity<String> firstResponse = restTemplate.postForEntity("/auth/register", request, String.class);

        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        ResponseEntity<String> secondResponse = restTemplate.postForEntity("/auth/register", request, String.class);

        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }

    @Test
    void shouldRejectWrongPassword() {
        Map<String, String> registerRequest = Map.of(
                        "email", "wrong-password@example.com",
                        "password", "Password123!",
                        "fullName", "Test User");

        restTemplate.postForEntity(
                        "/auth/register",
                        registerRequest,
                        String.class);

        Map<String, String> loginRequest = Map.of(
                        "email", "wrong-password@example.com",
                        "password", "WrongPassword123!");

        ResponseEntity<String> response = restTemplate.postForEntity(
                        "/auth/login",
                        loginRequest,
                        String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldRejectInvalidAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("this-is-not-a-valid-jwt");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                        "/auth/me",
                        HttpMethod.GET,
                        request,
                        String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}